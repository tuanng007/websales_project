$(document).ready(function() {
	$("#buttonCancel").on("click", function() { 
		window.location = moduleURL;
	});	
	
	$("#fileImage").on("change", function() {
		if(!checkFileSize(this)) { 
			return;
		}
		
		showImageThumbnail(this);
		
	});
});


function checkFileSize(fileInput) { 
	fileSize = fileInput.files[0].size;
	
	if(fileSize > MAX_FILE_SIZE){
		fileInput.setCustomValidity("You must choose an image less than " + MAX_FILE_SIZE + " bytes!");
		fileInput.reportValidity();
		
		return false;
	} else  { 
		fileInput.setCustomValidity("");
		return true;		
	}
}
	
function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	 var myModal = new bootstrap.Modal(document.getElementById('modalDialog'));
     myModal.show();
}

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) { 
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
}

$(document).ready(function(){
	$(".link-delete").on("click", function(e){
		e.preventDefault();
		link = $(this);
		entityName = link.attr("entityName");
		entityID= link.attr("entityID"); 		
		$("#yesButton").attr("href", link.attr("href"));
		$("#modalBody").text("Are you sure you want to delete this " + entityName + " ID " + entityID + " ?");
		let myModal = new bootstrap.Modal(document.getElementById('modalDialog'));
		myModal.show();
	});
});


	
