/*******************************************************************************
 * Copyright (c) 2009, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/ 
//
// Ad-hoc codes to bridge the gap between the HTML5 spec and browser implementations
//
if (true) { // if (typeof TextTrack == 'undefined') { // As of Mar 2015, no major browser sufficiently supports TextTrack.
  (function () {
    TextTrack = function (kind, label, language) { // See: http://www.w3.org/TR/html5/embedded-content-0.html#texttrack
      this.kind = kind;
      this.label = label;
      this.language = language;
      this.mode = TextTrack.HIDDEN;
      this.cues = new TextTrackCueList();
      this.activeCues = new TextTrackCueList();
    };

    TextTrack.DISABLED = 0;
    TextTrack.HIDDEN = 1;
    TextTrack.SHOWING = 2;

    TextTrack.prototype.addCue = function (cue) {
      cue.track = cue.__track__ = this;
      this.cues.__addCue__(cue);
    };

    TextTrack.prototype.removeCue = function (cue) {
      cue.track = cue.__track__ = null;
      this.cues.__removeCue__(cue);
    };

    TextTrack.prototype.toString = function () {
      return 'TextTrack <' + this.kind + '> ' + this.label + ' (' + this.language + ') : ' + this.cues;
    };

    TextTrackList = function () { // See: http://www.w3.org/TR/html5/embedded-content-0.html#texttracklist
      var list = [];

      list.getTrackById = function (id) {
        for (var i = 0; i < this.length; ++i) {
          var e = this[i];

          if (e.id == id) {
            return e;
          }
        }

        return null;
      };

      list.toString = function () {
        return 'TextTrackList [\n\t' + this.join(',\n\t') + '\n]';
      };

      return list;
    };

    TextTrackCue = function (startTime, endTime, text) { // See: http://www.w3.org/TR/html5/embedded-content-0.html#texttrackcue
      if (typeof VTTCue != 'undefined') {
        return new VTTCue(startTime, endTime, text);
      }
      else {
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = text;
      }
    };

    TextTrackCue.prototype.toString = function () {
      return 'TextTrackCue <' + this.id + '> ' + this.startTime + ' -> ' + this.endTime + ' : ' + this.text + (this.pauseOnExit ? ' * pause-on-exit * ' : ' ');
    };

    TextTrackCueList = function () { // See: http://www.w3.org/TR/html5/embedded-content-0.html#texttrackcuelist
      var list = [];

      list.getCueById = function (id) {
        for (var i = 0; i < this.length; ++i) {
          var e = this[i];

          if (e.id == id) {
            return e;
          }
        }

        return null;
      };

      list.__addCue__ = function (cue) {
        this.push(cue);
      };

      list.__removeCue__ = function (cue) {
        for (var i = 0; i < this.length; ++i) {
          if (this[i] === cue) {
            this.splice(i--, 1);
          }
        }
      };

      list.toString = function () {
        return 'TextTrackCueList [\n\t' + this.join(',\n\t') + '\n]';
      };

      return list;
    };

    var fixMediaElement = function (m) {
      m.textTracks = m.__textTracks__ = new TextTrackList();
      m.addTextTrack = m.__addTextTrack__ = addTextTrack;
      m.__lastTime__ = m.currentTime;
      m.addEventListener('timeupdate', updateCues, false);
      m.addEventListener('seeked', updateCues, false);
    };

    var fixTrackElement = function (t) {
      t.kind = t.getAttribute('kind');
      t.src = t.getAttribute('src');
      t.srclang = t.getAttribute('srclang');
      t.label = t.getAttribute('label');
      t['default'] = t.getAttribute('default') != null; // .default causes an error in IE

      if (!t.dataset) {
        var d = {};
        var a = t.attributes;
        
        for (var i = 0; i < a.length; ++i) {
          if (a[i].name.indexOf('data-') == 0) {
            d[a[i].name.substr(5)] = a[i].value;
          }
        }
        
        t.dataset = d;
      }
    };

    var addTextTrack = function (kind, label, language) { // HTMLMediaElement#addTextTrack
      var t = new TextTrack(kind, label, language);
      this.__textTracks__.push(t);
      return t;
    };

    var updateCues = function (e) { // http://dev.w3.org/html5/spec/media-elements.html#media-playback
      var video = e.target;

      var currentTime = video.currentTime;
      var lastTime = video.__lastTime__; video.__lastTime__ = currentTime;
      var isMonotonicIncrease = currentTime > lastTime && currentTime <= lastTime + .5; // major browsers fire timeupdate events every 250 ms

      var currentCues = [];
      var otherCues = [];
      var conflicted = false;

      var tracks = video.__textTracks__;

      for (var i = 0; i < tracks.length; ++i) {
        var t = tracks[i];

        if (t.mode != TextTrack.DISABLED) {
          var cues = t.cues;

          for (var j = 0; j < cues.length; ++j) {
            var c = cues[j];

            if (c.startTime <= currentTime && c.endTime > currentTime) {
              currentCues.push(c);
              c.__missed__ = false;
              conflicted = conflicted || !c.__active__;
            }
            else {
              otherCues.push(c);
              c.__missed__ = isMonotonicIncrease && c.startTime >= lastTime && c.endTime <= currentTime;
              conflicted = conflicted || c.__active__ || c.__missed__;
            }
          }
        }
      }

      if (!conflicted) {
        return;
      }

      var events = [];

      for (var i = 0; i < otherCues.length; ++i) {
        var c = otherCues[i];

        if (c.pauseOnExit && (c.__active__ || c.__missed__)) {
          video.pause();
        }

        if (c.__missed__) {
          events.push({ type: 'enter', timeStamp: c.startTime, target: c });
        }

        if (c.__active__ || c.__missed__) {
          events.push({ type: 'exit', timeStamp: c.endTime, target: c });
        }

        c.__active__ = false;
        c.__track__.activeCues.__removeCue__(c);
      }

      for (var i = 0; i < currentCues.length; ++i) {
        var c = currentCues[i];

        if (!c.__active__) {
          events.push({ type: 'enter', timeStamp: c.startTime, target: c });
        }

        c.__active__ = true;
        c.__track__.activeCues.__addCue__(c);
      }

      events.sort(function (a, b) { return a.timeStamp - b.timeStamp });

      for (var i = 0; i < events.length; ++i) {
        var e = events[i];
        var f = e.target['on' + e.type];

        if (f) {
          f.call(e.target, e);
        }
      }
    };

    var videoElems = document.getElementsByTagName('video');

    for (var i = 0; i < videoElems.length; ++i) {
      fixMediaElement(videoElems[i]);
    }

    var audioElems = document.getElementsByTagName('audio');

    for (var i = 0; i < audioElems.length; ++i) {
      fixMediaElement(audioElems[i]);
    }

    var trackElems = document.getElementsByTagName('track');

    for (var i = 0; i < trackElems.length; ++i) {
      var t = trackElems[i];
      fixTrackElement(t);
      t.track = t.__track__ = t.parentNode.__addTextTrack__(t.kind, t.label, t.srclang);
    }
  })();
}

if (true) { // if (typeof MediaController == 'undefined') { // As of Mar 2015, no major browser sufficiently supports MediaController.
  (function () {
    MediaController = function () {}; // See: http://www.w3.org/TR/html5/embedded-content-0.html#mediacontroller

    MediaController.prototype.__syncTime__ = function (kicker) {
      if (kicker.__seekedForSync__) {
        kicker.__seekedForSync__ = false;
        return;
      }
      
      var slaveElems = getSlaveElementsOf(this);
      var currentTime = kicker.currentTime;
      
      for (var i = 0; i < slaveElems.length; ++i) {
        var s = slaveElems[i];
        
        if (s !== kicker) {
          s.__seekedForSync__ = true;
          s.currentTime = currentTime;
        }
      }
    };

    MediaController.prototype.__syncPlayback__ = function (kicker) {
      var slaveElems = getSlaveElementsOf(this);
      var readyState = 0;
      var paused = false;

      for (var i = 0; i < slaveElems.length; ++i) {
        var s = slaveElems[i];
        readyState = Math.max(readyState, s.readyState);
        paused = paused || s.ended || (s.paused && !s.__pausedForSync__);
      }

      this.readyState = readyState;
      this.paused = paused || readyState < 4; // < HAVE_ENOUGH_DATA

      for (var i = 0; i < slaveElems.length; ++i) {
        var s = slaveElems[i];

        if (this.paused) {
          if (!s.paused) {
            s.__pausedForSync__ = true;
            s.pause();
          }
          else {

          }
        }
        else {
          if (s.paused) {
            s.__pausedForSync__ = false;
            s.play();
          }
          else {
            s.__pausedForSync__ = false;
          }
        }
      }
    };

    var getSlaveElementsOf = function (controller) {
      var videoElems = document.getElementsByTagName('video');
      var audioElems = document.getElementsByTagName('audio');
      var slaveElems = [];

      for (var i = 0; i < videoElems.length; ++i) {
        var v = videoElems[i];

        if (v.controller === controller) {
          slaveElems.push(v);
        }
      }

      for (var i = 0; i < audioElems.length; ++i) {
        var a = audioElems[i];

        if (a.controller === controller) {
          slaveElems.push(a);
        }
      }

      return slaveElems;
    };

    var controllers = {};
    
    var fixMediaElement = function (m) {
      m.mediaGroup = m.getAttribute('mediagroup');
      
      if (!m.mediaGroup) {
        return;
      }
      
      m.controller = controllers[m.mediaGroup] || (controllers[m.mediaGroup] = new MediaController());
      m.addEventListener('seeked', syncTime, false);
      m.addEventListener('seeked', syncPlayback, false);
      m.addEventListener('playing', syncPlayback, false);
      m.addEventListener('waiting', syncPlayback, false);
      m.addEventListener('pause', syncPlayback, false);
      m.addEventListener('ended', syncPlayback, false);
      m.__pausedForSync__ = true;
    };

    var syncTime = function (e) {
      e.target.controller.__syncTime__(e.target);
    };
    
    var syncPlayback = function (e) {
      e.target.controller.__syncPlayback__(e.target);
    };
    
    var videoElems = document.getElementsByTagName('video');

    for (var i = 0; i < videoElems.length; ++i) {
      fixMediaElement(videoElems[i]);
    }

    var audioElems = document.getElementsByTagName('audio');

    for (var i = 0; i < audioElems.length; ++i) {
      fixMediaElement(audioElems[i]);
    }
  })();
}

