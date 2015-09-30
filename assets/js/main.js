//API_KEY && API_SIG for flickr
var API_KEY = "8873397f084b2390be0fca6d0058c937";
var API_SIG = "e78c1066099b5954";

var url = "http://192.168.1.101:8080/web/";


var PicasaMgr = {

		/**
		 * return true if send successfully, else return false
		 * 
		 * data is a json object e.g. {a: "hello", b: "hi"}
		 * 
		 * callback(status), 
		 */
		sendReport : function(data, type, callback){
			var reportData = data;					
			
			if(!callback){
				callback = this._defaultCallback;
			}
			
			this._callback = callback;
		
			//send data timeout
			var timeOutId = setTimeout(this._requestTimeoutCallback, this._requestTimeout);
			
			var xmlHttpRequest = this._getXmlHttpRequest();
			alert(url);
			
			var reqParam = this._getRequestPara("POST", url);			
			
			xmlHttpRequest.open(reqParam.method, reqParam.url, reqParam.async);			
			xmlHttpRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");		
			
			xmlHttpRequest.onreadystatechange = function(){
				clearTimeout(timeOutId);
				if(this.readyState == 4){	
					alert(this.status);
					callback(this.status == 200);
				}
			};
			
			try{
				alert("send : "+ reportData);
				xmlHttpRequest.send(reportData);
			}catch(e){
				alert("send error:  "+e);
			}
			
		},
		
		_requestTimeoutCallback: function(){
			var xmlHttp = Logger.Reporter._getXmlHttpRequest();			
			if(xmlHttp != null){
				xmlHttp.onreadystatechange = null;
				xmlHttp.abort(); //doesn't work correctly
				Logger.Reporter._callback(false);
			}
		},
		
		/**
		 * do nothing
		 */
		_defaultCallback : function(status){
			alert(status);			
		},
		
		_getRequestPara : function(_method,_url){
			return {method : _method, url: _url, async : true};
		},
		
		_getXmlHttpRequest : function(){			
			var xmlHttpRequest = null;
			
			if (window.XMLHttpRequest) {
				xmlHttpRequest = new XMLHttpRequest();
			} else {
				var MSXML = ['MSXML2.XMLHTTP.5.0', 'MSXML2.XMLHTTP.4.0',
						'MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP', 'Microsoft.XMLHTTP'];
				for ( var i = 0; i < MSXML.length; i++) {
					try {
						xmlHttpRequest = new ActiveXObject(MSXML[i]);
					} catch (e) {
					}
				}
			}
			
			return xmlHttpRequest;
		},
		
		_callback : null,
		_requestTimeout : 5000
};

function test() {
	PicasaMgr.sendReport("111");
}
