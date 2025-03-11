/**
 * 
 */
(function(){
	var userInfo , preventivoDetail, preventivoList, productList , errorDisplay;
	var preventivo_list_div = document.getElementById("preventivo_list_div");
	var product_list_div = document.getElementById("product_list_div");
	var preventivo_detail_div = document.getElementById("preventivo_detail_div");
	var form_preventivo_div = document.getElementById("form_preventivo_div");
	
	var pageOrchestrator = new PageOrchestrator();
	var is_main_page;
	
	preventivo_detail_div.style.display = 'none';
	form_preventivo_div.style.display = 'none';
	
	window.addEventListener("load", () => {
		pageOrchestrator.start();
		pageOrchestrator.refresh();
	});
	
	function PageOrchestrator(){
		this.start = function(){
			is_main_page = true;
			userInfo = new UserInfo(
				document.getElementById("header_user_name"),
				document.getElementById("header_user_id"),
				document.getElementById("header_return_button"),
				document.getElementById("header_logout_button"),
				sessionStorage.getItem('name'), 
                sessionStorage.getItem('id'), 
                sessionStorage.getItem('permission')
                //TODO
			);
			
			
			preventivoDetail = new PreventivoDetail(
				document.getElementById("preventivo_detail_div"),
				document.getElementById("preventivo_detail_div_client_name"),
				document.getElementById("preventivo_detail_div_operator_name"),
				document.getElementById("preventivo_detail_div_product_name"),
				document.getElementById("preventivo_detail_div_img_url"),
				document.getElementById("preventivo_detail_div_creation_date"),
				document.getElementById("preventivo_detail_div_price"),
				document.getElementById("preventivo_detail_div_options")
			);
			
			preventivoList = new PreventivoList(
				document.getElementById("preventivo_list_div"),
				document.getElementById("preventivo_list_div_error")
			);
			
			productList = new ProductList(
				document.getElementById("product_list_div"),
				document.getElementById("product_list_div_error")
			);
			
			errorDisplay = new ErrorDisplay(
				document.getElementById("error_div"),
				document.getElementById("error_message"),
				document.getElementById("error_div_button")
			);
			
		};
		
		this.hide = function(){
			preventivoList.hide();
			productList.hide();
			errorDisplay.hide();
			preventivoDetail.hide();
			
		};
		
		this.refresh = function(){
			this.hide();
			userInfo.show();
			preventivoList.show();
			productList.show();
			
		};
		
		this.handleError = function(error){
			this.hide();
			errorDisplay.show(error);
		};
	}
	
	function ErrorDisplay(
		_error_div,
		_error_message,
		_error_div_button
	){
		this.error_div = _error_div;
		this.error_message = _error_message;
		this.error_div_button = _error_div_button;
		
		var self = this
		
		this.show = function(errorContent){
			self.error_message.textContent = errorContent;
			self.error_div_button.addEventListener("click", () => {
            	pageOrchestrator.refresh();
        	});
			self.error_div.style.display = 'block';
		};
		
		this.hide = function()
		{
			self.error_div.style.display = 'none';
		};
	}
	
	function UserInfo(
		_header_user_name,
		_header_user_id,
		_header_return_button,
		_header_logout_button,
		_name,
		_id,
		_permission
		
	)
	{
		this.name = _header_user_name;
		this.user_id = _header_user_id;
		this.return_button = _header_return_button;
		this.logout_button = _header_logout_button;
		
		this.logout_button.addEventListener("click", () => {
            sessionStorage.clear();
        });
        
        this.show = function(){
			this.name.textContent = _name;
			this.user_id.textContent = _id; 	
			
		};
		
		this.return_button.addEventListener("click", () => {
			if(!is_main_page)	// is not in main page
			{
				is_main_page = true;
				pageOrchestrator.refresh();
			}
			else{
				confirm("Sei gia nella pagina home.");	
			}
		});
		
		
		
	}
	
	function ProductList(
		_product_list_div,
		_product_list_div_error
	){
		this.product_list_div = _product_list_div;
		this.product_list_div_error = _product_list_div_error;
		var self = this;
		
		this.show = function(){
			makeCall("GET", 'GetProductList', null, (req) => {
				switch(req.status){
					case 200: //ok
                        var productList = JSON.parse(req.responseText);
                        self.update(productList);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.update(null, req.responseText);
                        break;
                    default: //Error
                        self.update(null, "Request reported status " + req.status);
                        break;
				}
			});
		}
		
		this.update = function(_productList, _error)
		{
			this.hide();
			self.product_list_div.innerHTML = '';
			self.product_list_div_error.style.display = 'none';
			if(_error){
				pageOrchestrator.handleError(_error);
			}
			else
			{
				if(_productList.length === 0){
					self.product_list_div_error.textContent = "Non hai alcune preventivo associato";
					self.product_list_div_error.style.display = "block";
				}
				else
				{
					
					let card, card_data, b1, br, detail_button;
						_productList.forEach((productPacket) => {
						card = document.createElement("div");
						card.className = "card card-blue";
						card_data = document.createElement("div");
                        card_data.className = "card-data";
                        
                        b1 = document.createElement("b");
                        b1.textContent = "Nome Prodotto: ";
                        card_data.appendChild(b1);
                        card_data.appendChild(document.createTextNode(productPacket.name));
                        
                        br = document.createElement("br");
                        card_data.appendChild(br);
                        
                     
                        card.appendChild(card_data);
                        
                        let card_data2 ,img , sendButton;
                        var form;
                        card_data2 = document.createElement("div");
                        //card_data2.className = "card";
                        img = document.createElement("img");
                        img.src = productPacket.imgUrl;
                        img.style = "width:100px;height:100px;"
                        card_data2.appendChild(img);
                        
                        form = document.createElement("form");
                        form.action = "#";
                        form.method = "post";
                        
                        productPacket.opsList.forEach((option) => {
							let div, input, label;
							
							div = document.createElement("div");
							input = document.createElement("input");
							input.type = "checkbox";
							input.name = "selectedOptions[]"
							input.value = option.id;
							input.textContent = option.name;
							
							label = document.createElement("label");
							label.htmlFor = "selectedOptions[]";
							label.textContent = option.name + ", " +option.type;
							
							div.appendChild(label);
							div.appendChild(input);
							form.appendChild(div);
						});
						
						card_data2.appendChild(form);
						
						
						
						sendButton = document.createElement("a");
						sendButton.className = "btn btn-purple btn-small btn-primary";
						sendButton.textContent = "Crea Preventivo";
			

						form.appendChild(sendButton);
						form.querySelector("a").addEventListener("click", (e) => {
						
							let checkBoxes = card_data2.querySelectorAll('input[type="checkbox"]:checked');
						
							
							
							//TODO controllare se almeno selezionato uno
							
							if(checkBoxes.length < 1 )
							{
								confirm("Selezionare almeno 1 opzione");
							}
							else
							{
								var par = "";
								checkBoxes.forEach((box)=> {
									par += "&selectedOptions="+box.value;
								});
								makeCall("POST", 'SendPreventivo?productId=' + productPacket.id + par, null, (req) => {
									switch(req.status){
										case 200: //ok
					                    pageOrchestrator.refresh();
					                        break;
					                    case 400: // bad request
					                    case 401: // unauthorized
					                    case 500: // server 
					                       	pageOrchestrator.handleError(req.reponseText);
					                        break;
					                    default: //Error
					                        pageOrchestrator.handleError("Request reported status " + req.status);
					                        break;
									}
								});
							}
							
							
							
						});
						
						card_data2.appendChild(form);
						
						card_data2.style.display = 'none';
						
                        
                        detail_button = document.createElement("a");
                        detail_button.className = "btn btn-purple btn-small btn-primary";
                        detail_button.textContent = "Detaglio";
                        detail_button.addEventListener("click", (e) => {
							if(e.target.textContent === "Detaglio")
							{
								e.target.textContent = "Nascondi";
								card_data2.style.display = 'block';
								console.log(card_data2);
							}
							else
							{
								e.target.textContent = "Detaglio";
								card_data2.style.display = 'none';
							}
						});
                      
                        
                       
						card.appendChild(detail_button);
						card.appendChild(card_data2);
						self.product_list_div.appendChild(card);
					});
					self.product_list_div.style.display = "block";
					
				}
			}
		}
		
		this.hide = function()
		{
			self.product_list_div.style.display = "none";
		}
		
	}
	
	function PreventivoList(
		_preventivo_list_div,
		_preventivo_list_div_error
		
	){
		this.preventivo_list_div = _preventivo_list_div;
		this.preventivo_list_div_error = _preventivo_list_div_error;
		var self = this;
		
		this.show = function(){
			makeCall("GET", 'GetClientPreventivoList', null, (req) => {
				switch(req.status){
					case 200: //ok
                        var preventivos = JSON.parse(req.responseText);
                        self.update(preventivos);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.update(null, req.responseText);
                        break;
                    default: //Error
                        self.update(null, "Request reported status " + req.status);
                        break;
				}
			});
		}
		
		this.hide = function(){
			self.preventivo_list_div.style.display = 'none';
		}
		
		
		
		this.update = function(_preventivos, _error){
			if(_error){
				pageOrchestrator.handleError(_error);
			}
			else
			{

				if(_preventivos.length === 0){
					self.preventivo_list_div_error.textContent = "Non hai alcune preventivo associato";
					self.preventivo_list_div_error.style.display = "block";
				}
				else
				{
					self.preventivo_list_div.innerHTML = '';
					self.preventivo_list_div_error.style.display = "none";
					let card, card_data, b1, b2, br, detail_button;
					_preventivos.forEach((preventivo) => {
						card = document.createElement("div");
						card.className = "card card-blue";
						card_data = document.createElement("div");
                        card_data.className = "card-data";
                        
                        b1 = document.createElement("b");
                        b1.textContent = "Data di Creazione: ";
                        card_data.appendChild(b1);
                        card_data.appendChild(document.createTextNode(preventivo.creationDate));
                        
                        br = document.createElement("br");
                        card_data.appendChild(br);
                        
                        b2 = document.createElement("b");
                        b2.textContent = "Product name: ";
                        card_data.appendChild(b2);
                        card_data.appendChild(document.createTextNode(preventivo.productName));
                        
                        card.appendChild(card_data);
                        detail_button = document.createElement("a");
                        detail_button.className = "btn btn-purple btn-small btn-primary";
                        detail_button.textContent = "Detaglio";
                        detail_button.setAttribute('preventivo_id', preventivo.id);
                        detail_button.addEventListener("click", (e) => {
							//TODO
							// passare preventivo.id all preventivo detail, e chiamare lo show
							pageOrchestrator.hide();
							preventivoDetail.show(preventivo.id);
						});
						card.appendChild(detail_button);
						self.preventivo_list_div.appendChild(card);
					});
				}
				self.preventivo_list_div.style.display = "block";
			}
		}
		
	}
	
	function PreventivoDetail(
		_preventivo_detail_div,
		_preventivo_detail_div_client_name,
		_preventivo_detail_div_operator_name,
		_preventivo_detail_div_product_name,
		_preventivo_detail_div_img_url,
		_preventivo_detail_div_creation_date,
		_preventivo_detail_div_price,
		_preventivo_detail_div_options
		
	){
		this.preventivo_detail_div = _preventivo_detail_div;
		this.preventivo_detail_div_client_name = _preventivo_detail_div_client_name;
		this.preventivo_detail_div_operator_name = _preventivo_detail_div_operator_name;
		this.preventivo_detail_div_product_name = _preventivo_detail_div_product_name;
		this.preventivo_detail_div_img_url = _preventivo_detail_div_img_url;
		this.preventivo_detail_div_creation_date = _preventivo_detail_div_creation_date;
		this.preventivo_detail_div_price = _preventivo_detail_div_price;
		this.preventivo_detail_div_options = _preventivo_detail_div_options;
		var self = this;
		
		this.show = function(preventivoId)
		{

            makeCall("GET", 'GetPreventivoDetails?preventivoId=' + preventivoId, null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        var data = JSON.parse(req.responseText);
                        self.update(data.preventive, data.listOption, false);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.update(null,null, req.responseText);
                        break;
                    default: //Error
                        self.update(null,null, "Request reported status " + req.status);
                        break;
                }
            });
		};
		
		this.hide = function(){
			self.preventivo_detail_div.style.display = "none";
		};
		
		this.update = function(_preventivo, _options, _error){
			if(_error)
			{
				pageOrchestrator.handleError(_error);
			}
			else
			{
				this.hide(); //Hide contente while updating
				this.preventivo_detail_div_client_name.textContent = _preventivo.clientName;
				this.preventivo_detail_div_product_name.textContent = _preventivo.productName;
				this.preventivo_detail_div_img_url.src = _preventivo.imgUrl;
				this.preventivo_detail_div_creation_date.textContent = _preventivo.creationDate;
				if(_preventivo.price > 0)
				{
					this.preventivo_detail_div_price.textContent = _preventivo.price;
					this.preventivo_detail_div_operator_name.textContent = _preventivo.operatorName;
				}
				else
				{
					this.preventivo_detail_div_price.textContent = "Ancora da decidere";
					this.preventivo_detail_div_operator_name.textContent = "Non è ancora associato ad un operatore";
				}
				self.preventivo_detail_div_options.innerHTML = '';
				_options.forEach((option) => {
					let div,b;
					div = document.createElement("div");
					b = document.createElement("b");
					b.textContent = "Opzioni: ";
					div.appendChild(b);
					let string = "option: "+ option.name + " tipo: " + option.type;
					div.appendChild(document.createTextNode(string));
					self.preventivo_detail_div_options.appendChild(div);
				});
				is_main_page = false;
				self.preventivo_detail_div.style.display = "block";
			}
		
		};
	}
	
})();