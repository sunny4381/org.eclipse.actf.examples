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
// Ad-hoc codes to bridge the gap in implementations between modern browsers and old IEs
//
(function () {
  var FORMAT_REPLACE = { ogv: 'wmv', oga: 'wma', mp4: 'wmv', m4a: 'wma' }; // for new Audio(src) and HTMLMediaElement#src (not for HTMLSourceElement#src)
  var TYPE_SUPPORTED = { 'video/x-ms-wmv': true, 'audio/x-ms-wma': true, 'video/mpeg': true, 'audio/mpeg': true }; // for HTMLSourceElement#type
  var MAX_PRELOADERS = 4;

  if (typeof XMLHttpRequest == 'undefined') {
    XMLHttpRequest = function () {
      return new ActiveXObject('MSXML2.XMLHTTP');
    };
  }

  globalEventListeners = {};

  var fixMediaElement = function (media) {
    var wmpMedia = createWMPMedia(media);
    media.appendChild(wmpMedia);
    media.__wmpMedia__ = wmpMedia;
    media.__eventListeners__ = {};
    media.addEventListener = addEventListener;
    media.dispatchEvent = dispatchEvent;
    media.play = play;
    media.pause = pause;
    media.paused = true;
    media.ended = false;
    media.volume = wmpMedia.settings.volume / 100;
    media.currentTime = media.__prevTime__ = wmpMedia.controls.currentPosition;

    createScript(wmpMedia.id, 'positionChange(oldPosition, newPosition)', 'globalEventListeners["' + wmpMedia.id + '"].positionChange(oldPosition, newPosition)');
    createScript(wmpMedia.id, 'playStateChange(NewState)', 'globalEventListeners["' + wmpMedia.id + '"].playStateChange(NewState)');

    globalEventListeners[wmpMedia.id] = {
      positionChange: function (oldPosition, newPosition) {
        media.dispatchEvent({ type: 'seeked', target: media });
      },
      
      playStateChange: function (NewState) {
        switch (NewState) {
        case 2: // Paused
          media.paused = true;
          media.ended = false;
          media.dispatchEvent({ type: 'pause', target: media });
          break;
        case 3: // Playing
          media.paused = false;
          media.ended = false;
          media.dispatchEvent({ type: 'playing', target: media });
          break;
        case 8: // MediaEnded
          media.paused = true;
          media.ended = true;
          media.dispatchEvent({ type: 'ended', target: media });
          break;
        case 6: case 7: // Buffering, Waiting
          media.dispatchEvent({ type: 'waiting', target: media });
          break;
        }
      }
    };

    if (media.getAttribute('preload') != 'none' && !media.__wmpMedia__.settings.autoStart) {
      preloadMedia(media);
    }

    media.addEventListener('seeked', function (e) {
      var media = e.target;

      if (media.ended) {
        media.__pausedForSync__ = true;
        media.ended = false;
      }
    }, false);
    
    var prevTime = wmpMedia.controls.currentPosition;

    setInterval(function () {
      var time = wmpMedia.controls.currentPosition;

      if (time != prevTime) {
        media.dispatchEvent({ type: 'timeupdate', target: media });
      }
      else {
        media.dispatchEvent({ type: '__noop__', target: media });
      }

      prevTime = time;
    }, 250);
  };

  var createScript = function (htmlFor, event, text) {
    var s = document.createElement('script');
    s.htmlFor = htmlFor;
    s.event = event;
    s.text = text;
    document.body.appendChild(s);
  };

  var addEventListener = function (type, handler, capture) {
    var v = this.__eventListeners__[type] || [];
    v.push(handler);
    this.__eventListeners__[type] = v;
  };

  var dispatchEvent = function (e) {
    setTimeout(function () {
      var media = e.target;

      if (media.currentTime != media.__prevTime__) {
        media.__wmpMedia__.controls.currentPosition = media.__prevTime__ = media.currentTime;
      }

      media.__prevTime__ = media.currentTime = media.__wmpMedia__.controls.currentPosition;
      media.__wmpMedia__.settings.volume = media.volume * 100;
      media.volume = media.__wmpMedia__.settings.volume / 100;
      var v = media.__eventListeners__[e.type] || [];

      for (var i = 0; i < v.length; ++i) {
        v[i].call(media, e);
      }
    }, 0);
  };

  var play = function () {
    this.__wmpMedia__.controls.currentPosition = this.currentTime;
    this.__wmpMedia__.controls.play();
    this.paused = false;
  };

  var pause = function () {
    this.currentTime = this.__wmpMedia__.controls.currentPosition;
    this.__wmpMedia__.controls.pause();
    this.paused = true;
  };

  var preloaders = 0;

  var preloadMedia = function (media) {
    (function () {
      if (preloaders < MAX_PRELOADERS) {
        var wmpFetch = createWMPFetch(media);
        media.appendChild(wmpFetch);
        ++preloaders;
        
        createScript(wmpFetch.id, 'openStateChange(NewState)', 'globalEventListeners["' + wmpFetch.id + '"].openStateChange(NewState)');
        createScript(wmpFetch.id, 'playStateChange(NewState)', 'globalEventListeners["' + wmpFetch.id + '"].playStateChange(NewState)');
        
        globalEventListeners[wmpFetch.id] = {
          openStateChange: function (NewState) {
            if (NewState == 13) { // MediaOpen
              --preloaders;
            }
          },
          
          playStateChange: function (NewState) {
            if (NewState == 3) { // Playing
              wmpFetch.controls.stop();
            }
          }
        };
      }
      else {
        setTimeout(arguments.callee, 1000);
      }
    })();
  };

  var fixDOMStructure = function (media) {
    var newMedia = document.createElement(media.outerHTML);
    var v = [];

    for (var e = media.nextSibling; e.tagName != '/' + media.tagName; e = e.nextSibling) {
      v.push(e);
    }

    for (var i = 0; i < v.length; ++i) {
      newMedia.appendChild(v[i]);
    }

    media.parentNode.replaceChild(newMedia, media);

    return newMedia;
  };

  var createWMPMedia = function (media) {
    var wmp = document.createElement('object');
    wmp.appendChild(createWMPParam('URL', getSourceURL(media)));
    wmp.appendChild(createWMPParam('autoStart', media.getAttribute('autoplay') !== null ? 'true' : 'false'));
    wmp.appendChild(createWMPParam('uiMode', media.getAttribute('controls') !== null ? 'full' : 'none'));
    wmp.appendChild(createWMPParam('mute', media.getAttribute('muted') !== null ? 'true' : 'false'));
    wmp.appendChild(createWMPParam('shrinkToFit', 'true'));
    wmp.appendChild(createWMPParam('stretchToFit', 'true'));
    wmp.appendChild(createWMPParam('windowlessVideo', 'true'));
    wmp.width = media.getAttribute('width') || 1;
    wmp.height = media.getAttribute('height') || 1;
    wmp.id = document.uniqueID;
    wmp.classid = 'CLSID:6BF52A52-394A-11D3-B153-00C04F79FAA6';
    wmp.settings.mute = media.getAttribute('muted') !== null; // <param name=mute> does not work (?)
    return wmp;
  };

  var createWMPFetch = function (media) {
    var wmp = document.createElement('object');
    wmp.appendChild(createWMPParam('URL', getSourceURL(media)));
    wmp.width = 1;
    wmp.height = 1;
    wmp.id = document.uniqueID;
    wmp.className = 'vd-prefetcher';
    wmp.classid = 'CLSID:6BF52A52-394A-11D3-B153-00C04F79FAA6';
    wmp.settings.mute = true;
    return wmp;
  };
  
  var createWMPParam = function (name, value) {
    var p = document.createElement('param');
    p.name = name;
    p.value = value;
    return p;
  };

  var getSourceURL = function (media) {
    if (media.src) {
      return media.src.replace(/\b\w+$/, function (e) {
        return FORMAT_REPLACE[e] || e;
      });
    }

    var v = media.getElementsByTagName('source');

    for (var i = 0; i < v.length; ++i) {
      if (TYPE_SUPPORTED[v[i].type]) {
        return v[i].src;
      }
    }

    return v[0].src;
  };
  
  if (typeof Audio == 'undefined') {
    Audio = function (url) {
      this.src = url;
      fixMediaElement(this);
    };

    Audio.prototype.appendChild = function (e) { document.body.appendChild(e) };
    Audio.prototype.getAttribute = function (_) { return null };
  }

  var videoElems = document.getElementsByTagName('video');

  for (var i = 0; i < videoElems.length; ++i) {
    fixMediaElement(fixDOMStructure(videoElems[i]));
  }
  
  var audioElems = document.getElementsByTagName('audio');

  for (var i = 0; i < audioElems.length; ++i) {
    fixMediaElement(fixDOMStructure(audioElems[i]));
  }
})();

