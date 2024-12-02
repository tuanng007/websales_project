var extraImagesCount = 0;
dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function(){
	
	$("#shortDescription").richText();
	$("#fullDescription").richText();

	
	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories();
	});
	getCategories();
	
	$("input[name='extraImage']").each(function(index) { 
		$(this).change(function() {
			extraImagesCount++;
			showExtraImageThumbnail(this, index);	
		});
	});


});

function showExtraImageThumbnail(fileInput, index) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) { 
		$("#extraThumbnail" + index).attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
	
	if(index >= extraImagesCount -1 ) { 
		addExtraNextImageSection(index  + 1);
	}
}

function addExtraNextImageSection(index) { 
	htmlExtraImage = `
		<div class="col border m-3 p-2" id="divExtraImage${index}">
			 	<div id="extraImageHeader${index}">
			 		<label>Extra Image #${index + 1}:</label>
		 		</div>
			 	<div>
			 		<img id="extraThumbnail${index}" alt="Extra image#${index + 1}" class="img-fluid" style="width: 300px" >
			 	</div>
			 	<div class="m-2">
			 		<input type="file"  name="extraImage" accept="image/png, image/jpeg" 
			 			onchange="showExtraImageThumbnail(this, ${index})">
			 	</div>
			 </div>
	`;
	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark float-right"
			href="javascript:removeExtraImage(${index - 1})" title = "Remove this image"></a>
	`;
	
	$("#divImageSection").append(htmlExtraImage);
	$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
	extraImagesCount++;
}
	
function removeExtraImage(index) {
	$("#divExtraImage" + index).remove();
	extraImagesCount--;
}
	
function getCategories() { 
		brandId = dropdownBrands.val();
		url = brandModuleURL + "/" + brandId + "/categories";
		
		$.get(url, function(responseJson){
			$.each(responseJson, function(index, category) {
				$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
			})
		});
	}

	
function checkNameUnique(form)  {
	
	productId = $("#id").val();
	productName = $("#name").val();
	csrfValue = $("input[name='_csrf']").val();
	
	params = {id: productId, name: productName, _csrf: csrfValue};
	
	$.post(checkUniqueURL, params, function(response) { 
		if(response == "OK") { 
			form.submit();
		} else if(response == "Duplicate") { 
			showWarningModal("There is another product having this name: " + productName);
		} else { 
			showErrorModal("Fail response from the server");
		}
	}).fail(function() { 
		showErrorModal("Could not connect the server ");

	});
	return false;
}

function showErrorModal(message) { 
	showModalDialog("Error", message);
}

function showWarningModal(message) { 
	showModalDialog("Warning", message);
}

	