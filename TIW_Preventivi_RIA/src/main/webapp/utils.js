/**
 */
(function (){
    var forms = document.getElementsByTagName("form");
    Array.from(forms).forEach(form => {
        var input_fields = form.querySelectorAll('input:not([type="button"]):not([type="hidden"])');
        var button = form.querySelector('input[type="button"]');
        Array.from(input_fields).forEach(input => {
            input.addEventListener("keydown", (e) => {
                if(e.keyCode == 13){
                    e.preventDefault();
                    let click = new Event("click");
                    button.dispatchEvent(click);
                }
            });
        });
    });
})();

/**
 * AJAX call management
 * -------------
 * Description: Make a call to server using AJAX, and report the results.
 * 
 * @param {*} method GET/POST method
 * @param {*} relativeUrl relative url of the call   
 * @param {*} form form to serialize and send. If null, empty request is sent.
 * @param {*} done_callback callback when the request is completed. Takes as argument an XMLHttpRequest
 * @param {*} reset Optionally reset the form (if provided). Default true
 */
function makeCall(method, relativeUrl, form, done_callback, reset = true) {
    var req = new XMLHttpRequest(); //Create new request
    //Init request
    req.onreadystatechange = function() {
        switch(req.readyState){
            case XMLHttpRequest.UNSENT:
                break;
            case XMLHttpRequest.OPENED:
                break;
            case XMLHttpRequest.HEADERS_RECEIVED:
            case XMLHttpRequest.LOADING:
                break;
            case XMLHttpRequest.DONE:
 
                if (checkRedirect(relativeUrl, req.responseURL)){ //Redirect if needed
                    done_callback(req);
                }
                setTimeout(null, 500);
                break;
        }
    };

  
    req.open(method, relativeUrl, true);
    //Send request

    if (form == null) {
        req.send(); //Send empty if no form provided
    } else if (form instanceof FormData){
        req.send(form); //Send already serialized form
    } else {
        req.send(new FormData(form)); //Send serialized form
    }
    //Eventually reset form (if provided)
    if (form !== null && !(form instanceof FormData) && reset === true) {
        form.reset(); //Do not touch hidden fields, and restore default values if any
    }
}

/**
 * Check if an AJAX call has been redirected.
 * This means that auth is no longer valid.
 * @param {*} requestURL Request relative url of the call
 * @param {*} responseURL Response url after eventual redirects
 * 
 * Notes:
 * - It's not possible to detect a redirect using response status code, as
 *   if a request is made to the same origin, or the server has CORS enabled,
 *   the 3XX response is followed transparently by XMLHttpRequest.
 *   This default behaviour is not overridable.
 *   (see https://xhr.spec.whatwg.org/#dom-xmlhttprequest-readystate)
 * 
 * - The value of req.responseURL will be the final URL obtained after any redirects.
 *   (see https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/responseURL)
 * 
 * - As pointed out here https://stackoverflow.com/questions/8056277/how-to-get-response-url-in-xmlhttprequest,
 *   req.responseURL could be empty when CORS request is blocked, or redirection loop is detected.
 */
function checkRedirect(requestURL, responseURL){
    if (responseURL){
        let actualRequestURL = relPathToAbs(requestURL);
        if (actualRequestURL != responseURL){ //Url changed
            window.location.assign(responseURL); //Navigate to the url
            return false;
        }
        return true; //Pass the request to callback
    }
    //Else is CORS blocked or redirection loop 
    console.error("Invalid AJAX call");
    return false;
}

/**
 * Relative/Absolute path
 * -------------
 * Description: Returns absolute path from relative
 *              (see https://stackoverflow.com/questions/14780350/convert-relative-path-to-absolute-using-javascript)
 * 
 * @param {*} relative Relative path for the request
 */
function relPathToAbs(relative) {
    var stack = window.location.href.split("/"),
        parts = relative.split("/");
    stack.pop(); // remove current file name (or empty string)
    for (var i=0; i<parts.length; i++) {
        if (parts[i] == ".")
            continue;
        if (parts[i] == "..")
            stack.pop(); //One directory back
        else
            stack.push(parts[i]); //Add to path
    }
    return stack.join("/"); //Join everything
}

/**
 * Utils for arrays
 * -------------
 * Description: Adding a modified version of standard include,
 *              with automatic cast during comparison.
 */
Array.prototype.contains = function(element){ 
    for(let i = 0;i<this.length;i++)
        if (this[i] == element)
            return true;
    
    return false;
}