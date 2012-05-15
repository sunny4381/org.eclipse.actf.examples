/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/ 
//
// Plays video descriptions (including extended video descriptions and external audio fragments) on the draft HTML5 spec
//
(function () {
  var TEXT_RESOURCES = { playLabel: '\u518d\u751f', pauseLabel: '\u4e00\u6642\u505c\u6b62', stopLabel: '\u505c\u6b62', skipLabel: '10\u79d2\u9032\u3080', backLabel: '10\u79d2\u623b\u308b', volUpLabel: '\u97f3\u91cf\u5927', volDnLabel: '\u97f3\u91cf\u5c0f', timeFormat: '%m\u5206%s\u79d2', showTextLabel: '\u30c6\u30ad\u30b9\u30c8\u3092\u8868\u793a' };
  var FORMAT_REPLACE = { MSIE: 'm4a', AppleWebKit: 'm4a', _: 'oga' }; // formats for .* external audio resources (e.g., 'foo.*' is replaced with 'foo.m4a' on MSIE)

  // Parses external text track resources
  //
  var TextTrackParser = [];

  TextTrackParser.read = function (xhr) {
    for (var i = 0; i < this.length; ++i) {
      var r = this[i];
      if (r.isReadable(xhr)) {
        return r.readCues(xhr);
      }
    }

    return null;
  };

  TextTrackParser.register = function (name, isReadable, readCues) {
    this.push({ name: name, isReadable: isReadable, readCues: readCues });
  };

  // TTML (ignores layout and styles)
  //
  (function () {
    var TTML_NAMESPACE = 'http://www.w3.org/ns/ttml';
    var XML_NAMESPACE = 'http://www.w3.org/XML/1998/namespace';
    var TVD_NAMESPACE = 'http://www.eclipse.org/actf/ai/tvd';

    var frameRate = 30;
    var subFrameRate = 1;

    var timeToSecond = function (exp) {
      if (!exp) return null;
      var v = exp.match(/^(\d{2,}):(\d{2}):(\d{2})(?:\.(\d+)|:(\d{2})(?:\.(\d+))?)?$/);
      var h = v[1] * 60 * 60;
      var m = v[2] * 60;
      var s = parseFloat(v[3] + '.' + (v[4] || 0));
      var f = parseInt(v[5] || 0) / frameRate;
      var u = parseInt(v[6] || 0) / frameRate / subFrameRate;
      return h + m + s + f + u;
    };

    var wildcardToFormat = function (url) {
      if (!url) return null;
      var v = navigator.userAgent.match(/MSIE|AppleWebKit/) || ['_'];
      var f = FORMAT_REPLACE[v[0]];
      return url.replace('*', f);
    };
    
    var isTTML = function (xhr) {
      var xml = xhr.responseXML;
      if (!xml) return false;
      var tts = xml.getElementsByTagName('tt'); // xml.getElementsByTagNameNS(TTML_NAMESPACE, 'tt'); // for IE
      return tts.length == 1;
    };

    var readCues = function (xhr) {
      var xml = xhr.responseXML;
      var ps = xml.getElementsByTagName('p'); // xml.getElementsByTagNameNS(TTML_NAMESPACE, 'p'); // for IE
      var cues = [];

      for (var i = 0; i < ps.length; ++i) {
        var p = ps[i];
        var id = p.getAttribute('xml:id'); // p.getAttributeNS(XML_NAMESPACE, 'id'); // for IE
        var begin = timeToSecond(p.getAttribute('begin'));
        var end = timeToSecond(p.getAttribute('end'));
        var dur = timeToSecond(p.getAttribute('dur'));
        var text = p.textContent || p.firstChild.nodeValue;
        var extended = p.getAttribute('tvd:extended') == 'true'; // p.getAttributeNS(TVD_NAMESPACE, 'extended') == 'true'; // for IE
        var external = wildcardToFormat(p.getAttribute('tvd:external')); // p.getAttributeNS(TVD_NAMESPACE, 'external'); // for IE
        end = end || (begin + (extended ? 0 : dur));
        dur = dur || (end - begin);
        var cue = new TextTrackCue(id, begin, end, text, '', extended);
        cue.__duration__ = dur;
        cue.__external__ = external;
        cues.push(cue);
      }

      return cues;
    };

    TextTrackParser.register('TTML', isTTML, readCues);
  })();

  // WebVTT (ignores layout and styles)
  //
  (function () {
    var timeToSecond = function (exp) {
      if (!exp) return null;
      var v = exp.match(/^(?:(\d{2,}):)?(\d{2}):(\d{2}\.\d{3})$/);
      var h = (v[1] || 0) * 60 * 60;
      var m = v[2] * 60;
      var s = parseFloat(v[3]);
      return h + m + s;
    };

    var wildcardToFormat = function (url) {
      if (!url) return null;
      var v = navigator.userAgent.match(/MSIE|AppleWebKit/) || ['_'];
      var f = FORMAT_REPLACE[v[0]];
      return url.replace('*', f);
    };

    var isWebVTT = function (xhr) {
      var txt = xhr.responseText;
      return txt.match(/^WEBVTT\b/);
    };

    var readCues = function (xhr) {
      var txt = xhr.responseText.replace(/\r\n|\r/g, '\n');
      var elems = txt.split(/\n{2,}/);
      var cues = [];

      for (var i = 0; i < elems.length; ++i) {
        var e = elems[i];
        var m = e.match(/^(?:(\w+)\n)?((?:\d{2,})?:\d{2}:\d{2}\.\d{3})[ \t]+-->[ \t]+((?:\d{2,})?:\d{2}:\d{2}\.\d{3})(?:[ \t]+([^\n]+))?\n([\w\W]+)$/m);
        
        if (m) {
          var id = m[1] || '';
          var begin = timeToSecond(m[2]);
          var end = timeToSecond(m[3]);
          var settings = m[4] || '';
          var extended = settings.match(/\bextended\b/);
          var text = m[5];
          var m1 = settings.match(/\bduration=((?:\d{2,}:)?\d{2}:\d{2}\.\d{3})/);
          var dur = m1 ? timeToSecond(m1[1]) : 0;
          var m2 = settings.match(/\bexternal=([^,]+)/);
          var external = wildcardToFormat(m2 ? m2[1] : '');
          end = end || (begin + (extended ? 0 : dur));
          dur = dur || (end - begin);
          var cue = new TextTrackCue(id, begin, end, text, '', extended);
          cue.__duration__ = dur;
          cue.__external__ = external;
          cues.push(cue);
        }
      }

      return cues;
    };

    TextTrackParser.register('WebVTT', isWebVTT, readCues);
  })();

  // Shows/reads aloud captions/descriptions
  //
  var initCues = function (track, video) {
    var onenter = function (e) {
      var cue = e.target;

      if (cue.track.__kind__ == 'descriptions') {
        if (cue.__audio__) { // external resource exists
          cue.__audio__.currentTime = 0;
          cue.__audio__.play();
          video.__descriptions__.setAttribute('aria-live', 'off');
          showText(cue.text, video.__descriptions__);
        }
        else {               // uses screen reader
          video.__descriptions__.setAttribute('aria-live', 'assertive');
          showText(cue.text, video.__descriptions__);
        }
      }
      else if (cue.track.__kind__ == 'captions') {
        showText(cue.text, video.__captions__);
      }
    };

    var onexit = function (e) {
      var cue = e.target;

      if (cue.track.__kind__ == 'descriptions') {
        var isExtended = cue.pauseOnExit && video.paused;

        if (isExtended) {
          if (cue.__audio__) {
            if (cue.__audio__.ended) {
              showText('', video.__descriptions__);
              video.play();
            }
            else {
              cue.__audio__.addEventListener('ended', function () {
                showText('', video.__descriptions__);
                video.play();
              }, false);
            }
          }
          else {
            if (cue.endTime - cue.startTime >= cue.__duration__) {
              showText('', video.__descriptions__);
              video.play();
            }
            else {
              setTimeout(function () {
                showText('', video.__descriptions__);
                video.play();
              }, (cue.__duration__ - cue.endTime + cue.startTime) * 1000);
            }
          }
        }
        else {
          if (cue.__audio__) {
            cue.__audio__.pause();
            showText('', video.__descriptions__);
          }
          else {
            showText('', video.__descriptions__);
          }
        }
      }
      else if (cue.track.__kind__ == 'captions') {
        showText('', video.__captions__);
      }
    };

    var showText = function (text, container) {
      var oldText = container.firstChild;
      var newText = document.createElement('span');
      newText.appendChild(document.createTextNode(text));
      container.replaceChild(newText, oldText);
    };
    
    var cues = track.cues;

    for (var i = 0; i < cues.length; ++i) {
      var c = cues[i];

      if (c.__external__) {
        c.__audio__ = new Audio(c.__external__);
      }

      c.onenter = onenter;
      c.onexit = onexit;
    }
  };

  var loadCues = function (track, video) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
      if (xhr.readyState == 4) {
        switch (xhr.status) {
        case 200:
        case 0: // for local files
          var cues = TextTrackParser.read(xhr);

          if (cues) {
            for (var i = 0; i < cues.length; ++i) {
              track.track.addCue(cues[i]);
            }

            track.track.mode = track['default'] ? TextTrack.SHOWING : TextTrack.DISABLED; // .default causes an error in IE
            initCues(track.track, video);
          }
          
          break;
        }
      }
    };

    xhr.open('GET', track.src, true);
    xhr.send(null);
  };

  // Sets up control buttons and caption/description display areas
  //
  var createVDControls = function (video) {
    var paused = video.paused;

    var e = document.createElement('div');
    e.className = 'vd-controls';
    video.__controls__ = video.parentNode.appendChild(e);

    var toggleButton = e.appendChild(document.createElement('div')).appendChild(document.createElement('button'));
    toggleButton.appendChild(document.createTextNode(''));
    toggleButton.accessKey = 'p';
    toggleButton.onclick = function () {
      if (paused) {
        this.__update__(paused = false);
        video.play();
      }
      else {
        this.__update__(paused = true);
        video.pause();
      }
    };
    toggleButton.__update__ = function () {
      this.firstChild.nodeValue = paused ? TEXT_RESOURCES.playLabel : TEXT_RESOURCES.pauseLabel;
    };
    toggleButton.__update__();

    var stopButton = e.appendChild(document.createElement('div')).appendChild(document.createElement('button'));
    stopButton.appendChild(document.createTextNode(TEXT_RESOURCES.stopLabel));
    stopButton.accessKey = 's';
    stopButton.onclick = function () {
      video.currentTime = 0;
      video.play();
      video.pause();
      toggleButton.__update__(paused = true);
    };

    var backButton = e.appendChild(document.createElement('div')).appendChild(document.createElement('button'));
    backButton.appendChild(document.createTextNode(TEXT_RESOURCES.backLabel));
    backButton.accessKey = 'b';
    backButton.onclick = function () {
      video.currentTime -= 10;
      video.play();
      toggleButton.__update__(paused = false);
    };

    var skipButton = e.appendChild(document.createElement('div')).appendChild(document.createElement('button'));
    skipButton.appendChild(document.createTextNode(TEXT_RESOURCES.skipLabel));
    skipButton.accessKey = 'f';
    skipButton.onclick = function () {
      video.currentTime += 10;
      video.play();
      toggleButton.__update__(paused = false);
    };

    var volDnButton = e.appendChild(document.createElement('div')).appendChild(document.createElement('button'));
    volDnButton.appendChild(document.createTextNode(TEXT_RESOURCES.volDnLabel));
    volDnButton.accessKey = 'n';
    volDnButton.onclick = function () {
      video.volume -= .1;
    };

    var volUpButton = e.appendChild(document.createElement('div')).appendChild(document.createElement('button'));
    volUpButton.appendChild(document.createTextNode(TEXT_RESOURCES.volUpLabel));
    volUpButton.accessKey = 'u';
    volUpButton.onclick = function () {
      video.volume += .1;
    };

    var formatTime = function (t) {
      var m = parseInt(t / 60);
      var s = parseInt(t % 60);
      return TEXT_RESOURCES.timeFormat.replace('%m', m).replace('%s', s);
    };

    var timeDisplay = e.appendChild(document.createElement('div')).appendChild(document.createElement('span'));
    timeDisplay.appendChild(document.createTextNode(formatTime(0)));

    video.addEventListener('timeupdate', function (e) {
      timeDisplay.firstChild.nodeValue = formatTime(e.target.currentTime);
    }, false);

    video.addEventListener('ended', function (e) {
      toggleButton.__update__(paused = true);
    }, false);

    video.addEventListener('playing', function (e) {
      toggleButton.disabled = false;
      stopButton.disabled = false;
      backButton.disabled = false;
      skipButton.disabled = false;
    }, false);

    video.addEventListener('pause', function (e) {
      if (paused) { // paused by user (not for extended descriptions)
        return;
      }
      toggleButton.disabled = true;
      stopButton.disabled = true;
      backButton.disabled = true;
      skipButton.disabled = true;
    }, false);
  };

  var createVDDisplays = function (video) {
    var e1 = document.createElement('div');
    e1.className = 'vd-descriptions';
    e1.appendChild(document.createElement('span'));
    video.__descriptions__ = video.parentNode.appendChild(e1);
    
    var e2 = document.createElement('div');
    e2.className = 'vd-captions';
    e2.appendChild(document.createElement('span'));
    video.__captions__ = video.parentNode.appendChild(e2);

    var e1opt = document.createElement('div');
    e1opt.className = 'vd-descriptions-config';
    var l1 = document.createElement('label');
    var c1 = document.createElement('input');
    c1.type = 'checkbox';
    c1.onclick = function () {
      if (this.checked) {
        video.__descriptions__.className = [video.__descriptions__.className, 'vd-show'].join(' ');
      }
      else {
        video.__descriptions__.className = video.__descriptions__.className.replace(/\bvd-show\b/g, '');
      }
    };
    l1.appendChild(c1);
    l1.appendChild(document.createTextNode(TEXT_RESOURCES.showTextLabel));
    e1opt.appendChild(l1);
    video.parentNode.appendChild(e1opt);
  }

  var fixVideoElement = function (video) {
    var container = document.createElement('div');
    container.className = 'vd-container';
    container.appendChild(video.parentNode.replaceChild(container, video));
    createVDControls(video);
    createVDDisplays(video);
  };

  var videoElems = document.getElementsByTagName('video');

  for (var i = 0; i < videoElems.length; ++i) {
    var v = videoElems[i];
    fixVideoElement(v);
    var trackElems = v.getElementsByTagName('track');

    for (var j = 0; j < trackElems.length; ++j) {
      var t = trackElems[j];

      if (t.kind == 'descriptions' || t.kind == 'captions' || t.kind == 'metadata' && (t.dataset.kind == 'descriptions' || t.dataset.kind == 'captions')) {
        t.track.__kind__ = t.dataset.kind || t.kind;
        loadCues(t, v);
      }
    }
  }
})();

