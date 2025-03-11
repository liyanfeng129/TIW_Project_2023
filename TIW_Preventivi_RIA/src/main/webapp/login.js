/**
 * 
 */

(function() { 
  	var loginDiv = document.getElementById("login_div");
  	var loginButton = document.getElementById("login_button");
  	var loginWarning = document.getElementById("login_warning");
  	var openSignup = document.getElementById("open_signup");
  	var signupDiv = document.getElementById("signup_div"); 
  	var signupButton = document.getElementById("signup_button");
  	var signupWarning = document.getElementById("signup_warning");
  	var signupBackButton = document.getElementById("signup_back_button");
  	var passwordInput = signupButton.closest("form").querySelector('input[name="password"]');
  	var passwordRepeat = signupButton.closest("form").querySelector('input[name="passwordConfirm"]');
  	
  	signupDiv.style.display = 'none';
  	signupWarning.style.display = 'none';
  	
  	loginButton.addEventListener("click", (e) => {
		var form = e.target.closest("form");
		loginWarning.style.display = 'none';
		if(form.checkValidity()){ //if input format is correct
			console.log("input form is correct");
			sendToServer(form, loginWarning, 'Login');
		}else
			form.reportValidity();
	});
	
	signupButton.addEventListener("click", (e) => {
		var form = e.target.closest("form");
		signupWarning.style.display = 'none';
		if(form.checkValidity()){
			//Double check password
			if(passwordRepeat.value != passwordInput.value)
			{
				signupWarning.textContent = "Le due password non sono identiche";
				signupWarning.style.display = 'block';
				return;
			}
			var formData = new FormData(form);
		
			var url = "Signup?name="+formData.get("name")+
						"&email="+formData.get("email")+
						"&password="+formData.get("password")+
						"&permission="+formData.get("permission");
			sendToServer(null, signupWarning, url)
		}
		else
			form.reportValidity();
	});
	
	openSignup.addEventListener("click", () => {
		loginDiv.style.display = 'none';
		signupDiv.style.display = 'block';
	});
	
	signupBackButton.addEventListener("click", () => {
		loginDiv.style.display = 'block';
		signupDiv.style.display = 'none';
	});
	
	
	
	
  
  
      function sendToServer(form, error_div, request_url){
        makeCall("POST", request_url, form, function(req){
			console.log("makeCall");
            switch(req.status){ //Get status code
                case 200: //Okay
                    var data = JSON.parse(req.responseText);
                    sessionStorage.setItem('id', data.id);
                    sessionStorage.setItem('name', data.name);
                    sessionStorage.setItem('permission', data.permission);
                    if(data.permission === "client")
                    	window.location.href = "home_client.html";
                    else
                    	window.location.href = "home_admin.html";
                    break;
                case 400: // bad request
                case 401: // unauthorized
                case 500: // server error
                    error_div.textContent = req.responseText;
                    error_div.style.display = 'block';
                    break;
                default: //Error
                    error_div.textContent = "Request reported status " + req.status;
                    error_div.style.display = 'block';
            }
        });
    }
    
})();