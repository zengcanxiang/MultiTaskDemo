!function(e) {
    var intervalTime = 3000; //ms
    var hrefUrl = e.location.href;
    var hostname = e.location.hostname;
    var pathname = e.location.pathname;
    var host = e.location.host;
    var pageTime = (new Date).getTime();

    e.startWebViewMonitor = function() {

        // 已经开始执行了就返回
        if (e.monitorStarted) return !1;
        e.monitorStarted = !0;

        setTimeout(function() {
            var navigationTiming = {
                type: "monitor_resourceTiming",
                payload: {
                    url: hrefUrl,
                    domain: hostname,
                    uri: pathname,
                    navigationTiming: performanceTiming.getNavigationTiming(),
                    resourceTiming: performanceTiming.getResourceTiming()
                }
            };
            sendResourceTiming(navigationTiming);
        }, 0);

        var getResourceTiming = function() {
            var timing = performanceTiming.getResourceTiming();
            if (timing.length > 0) {
                var resourceTiming = {
                    type: "monitor_resourceTiming",
                    payload: {
                        url: hrefUrl,
                        domain: hostname,
                        uri: pathname,
                        navigationTiming: {},
                        resourceTiming: timing,
                    }
                };
                sendResourceTiming(resourceTiming);
            }
        };

        e.setInterval(getResourceTiming, intervalTime); //每隔3秒执行一次

        var already = !0;
        e.addEventListener("beforeunload",
            function() {
                already && (already = !1, getResourceTiming())
            }
        );
        e.addEventListener("unload",
            function() {
                already && (already = !1, getResourceTiming())
            }
        );
    }

    function sendResourceTiming(e) {
        if(hrefUrl != "about:blank"){
            monitorNative.trackPerformance(JSON.stringify(e))
        }
    };

    var performanceTiming = function() {
        function navigationTiming() {
            if (!e.performance || !e.performance.timing) return {};
            var time = e.performance.timing;
            return {
                navigationStart: time.navigationStart,
                redirectStart: time.redirectStart,
                redirectEnd: time.redirectEnd,
                fetchStart: time.fetchStart,
                domainLookupStart: time.domainLookupStart,
                domainLookupEnd: time.domainLookupEnd,
                connectStart: time.connectStart,
                secureConnectionStart: time.secureConnectionStart ? time.secureConnectionStart: time.connectEnd - time.secureConnectionStart,
                connectEnd: time.connectEnd,
                requestStart: time.requestStart,
                responseStart: time.responseStart,
                responseEnd: time.responseEnd,
                unloadEventStart: time.unloadEventStart,
                unloadEventEnd: time.unloadEventEnd,
                domLoading: time.domLoading,
                domInteractive: time.domInteractive,
                domContentLoadedEventStart: time.domContentLoadedEventStart,
                domContentLoadedEventEnd: time.domContentLoadedEventEnd,
                domComplete: time.domComplete,
                loadEventStart: time.loadEventStart,
                loadEventEnd: time.loadEventEnd,
                pageTime: pageTime
            }
        }
        function resourceTiming() {
            if (!e.performance || !e.performance.getEntriesByType) return [];
            for (var time = e.performance.getEntriesByType("resource"), resArr = [], i = 0; i < time.length; i++) {
                var t = time[i].secureConnectionStart ? time[i].secureConnectionStart: time[i].connectEnd - time[i].secureConnectionStart,
                    res = {
                        connectEnd: time[i].connectEnd,
                        connectStart: time[i].connectStart,
                        domainLookupEnd: time[i].domainLookupEnd,
                        domainLookupStart: time[i].domainLookupStart,
                        duration: time[i].duration,
                        entryType: time[i].entryType,
                        fetchStart: time[i].fetchStart,
                        initiatorType: time[i].initiatorType,
                        name: time[i].name,
                        redirectEnd: time[i].redirectEnd,
                        redirectStart: time[i].redirectStart,
                        requestStart: time[i].requestStart,
                        responseEnd: time[i].responseEnd,
                        responseStart: time[i].responseStart,
                        secureConnectionStart: t,
                        startTime: time[i].startTime
                    };
                resArr.push(res);
            }
            return resArr;
        }
        return {
            cacheResourceTimingLength: 0,
            getNavigationTiming: function() {
                return navigationTiming();
            },
            getResourceTiming: function() {
                var timing = resourceTiming();
                var len = timing.length;
                return timing.length != this.cacheResourceTimingLength ?
                    (timing = timing.slice(this.cacheResourceTimingLength, len), this.cacheResourceTimingLength = len, timing) : []
            }
        }
    }();

} (this);
