	$(document).ready(function() {
		$("#buttonCancel").on("click", function() { 
			window.location = moduleURL;
		});	
		
		$("#fileImage").on("change", function() {
			fileSize = this.files[0].size;
			alert("File size: " + fileSize);
			
			if(fileSize > 1048576){
				this.setCustomValidity("You must choose an image less than 1MB!");
				this.reportValidity();
			}else {
				this.setCustomValidity("");
				showImageThumbnail(this);
			}
			
		});
	});
	
	
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
	

	
