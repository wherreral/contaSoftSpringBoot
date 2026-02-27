/**
 * Reusable file upload drag-and-drop widget.
 *
 * @param {Object} options
 * @param {string} options.containerId - ID of the DOM element where the widget renders.
 * @param {Array|null} options.taxpayers - Array of {id, name, rut} or null.
 *        null = any detected RUT is valid (charges page).
 *        Array = detected RUT must match a taxpayer (home page).
 * @param {Function} options.onUploadSuccess - Callback with JSON response on success.
 * @param {string} [options.uploadUrl='/importBookAjax'] - POST endpoint.
 */
function initUploadWidget(options) {
	var container = document.getElementById(options.containerId);
	if (!container) return;

	var uploadUrl = options.uploadUrl || '/importBookAjax';
	var taxpayersList = options.taxpayers || null;
	var onSuccess = options.onUploadSuccess || function() { location.reload(); };

	// Internal state
	var selectedFile = null;
	var detectedRut = null;
	var detectedTaxpayer = null;

	// Unique element IDs based on container
	var pfx = options.containerId;
	var fileInputId = pfx + '-fileInput';
	var dropZoneId = pfx + '-dropZone';
	var filePreviewId = pfx + '-filePreview';
	var fileNameId = pfx + '-fileName';
	var fileSizeId = pfx + '-fileSize';
	var detectedRutId = pfx + '-detectedRut';
	var fileIconId = pfx + '-fileIcon';
	var uploadBtnId = pfx + '-uploadBtn';
	var removeBtnId = pfx + '-removeBtn';

	// Render HTML
	container.innerHTML =
		'<input type="file" id="' + fileInputId + '" accept=".csv,.xls,.xlsx,.xlsm" style="display: none;">' +
		'<div class="drop-zone-compact" id="' + dropZoneId + '">' +
			'<i class="bi bi-cloud-arrow-up drop-icon"></i>' +
			'<div>' +
				'<p class="drop-text"><strong>Importar archivo</strong></p>' +
				'<p class="drop-formats">RUT en nombre (ej: 12345678-9.csv)</p>' +
			'</div>' +
		'</div>' +
		'<div class="file-preview-compact" id="' + filePreviewId + '">' +
			'<i class="bi bi-file-earmark-check file-icon" id="' + fileIconId + '"></i>' +
			'<div class="file-details">' +
				'<div class="file-name" id="' + fileNameId + '">archivo.csv</div>' +
				'<div class="file-size" id="' + fileSizeId + '">0 KB</div>' +
				'<div class="detected-rut" id="' + detectedRutId + '"></div>' +
			'</div>' +
			'<div class="file-actions">' +
				'<button class="btn btn-success btn-sm" id="' + uploadBtnId + '">' +
					'<i class="bi bi-upload"></i>' +
				'</button>' +
				'<button class="btn btn-outline-danger btn-sm" id="' + removeBtnId + '">' +
					'<i class="bi bi-x"></i>' +
				'</button>' +
			'</div>' +
		'</div>';

	// Element references
	var fileInput = document.getElementById(fileInputId);
	var dropZone = document.getElementById(dropZoneId);
	var filePreview = document.getElementById(filePreviewId);
	var uploadBtn = document.getElementById(uploadBtnId);
	var removeBtn = document.getElementById(removeBtnId);

	// Extract RUT from filename
	function extractRutFromFilename(filename) {
		var patterns = [
			/(\d{1,2}\.?\d{3}\.?\d{3}-[\dkK])/i,
			/(\d{7,8}-[\dkK])/i
		];
		for (var i = 0; i < patterns.length; i++) {
			var match = filename.match(patterns[i]);
			if (match) {
				return match[1].replace(/\./g, '').toUpperCase();
			}
		}
		return null;
	}

	// Find taxpayer by RUT
	function findTaxpayerByRut(rut) {
		if (taxpayersList === null) {
			// No list provided: any detected RUT is valid
			return { id: 0, name: 'Cliente Actual', rut: rut };
		}
		var normalizedRut = rut.replace(/\./g, '').toUpperCase();
		for (var i = 0; i < taxpayersList.length; i++) {
			var t = taxpayersList[i];
			var taxpayerRut = t.rut.replace(/\./g, '').toUpperCase();
			if (taxpayerRut === normalizedRut) {
				return t;
			}
		}
		return null;
	}

	// Format file size
	function formatFileSize(bytes) {
		if (bytes === 0) return '0 Bytes';
		var k = 1024;
		var sizes = ['Bytes', 'KB', 'MB', 'GB'];
		var i = Math.floor(Math.log(bytes) / Math.log(k));
		return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
	}

	// Process selected file
	function handleFile(file) {
		var validExtensions = ['.csv', '.xls', '.xlsx', '.xlsm'];
		var fileName = file.name.toLowerCase();
		var isValid = validExtensions.some(function(ext) { return fileName.endsWith(ext); });

		if (!isValid) {
			alert('Formato de archivo no soportado. Por favor use CSV, XLS, XLSX o XLSM.');
			return;
		}

		selectedFile = file;
		detectedRut = extractRutFromFilename(file.name);
		detectedTaxpayer = detectedRut ? findTaxpayerByRut(detectedRut) : null;

		// Update preview
		document.getElementById(fileNameId).textContent = file.name;
		document.getElementById(fileSizeId).textContent = formatFileSize(file.size);

		var detectedRutEl = document.getElementById(detectedRutId);
		var fileIcon = document.getElementById(fileIconId);

		if (detectedTaxpayer) {
			detectedRutEl.textContent = '\u2713 ' + detectedTaxpayer.name;
			detectedRutEl.className = 'detected-rut valid';
			fileIcon.className = 'bi bi-file-earmark-check file-icon';
			fileIcon.style.color = '#28a745';
			uploadBtn.disabled = false;
		} else if (detectedRut) {
			detectedRutEl.textContent = '\u2717 RUT ' + detectedRut + ' no encontrado';
			detectedRutEl.className = 'detected-rut invalid';
			fileIcon.className = 'bi bi-file-earmark-x file-icon';
			fileIcon.style.color = '#dc3545';
			uploadBtn.disabled = true;
		} else {
			detectedRutEl.textContent = '\u2717 No se detect\u00f3 RUT en el nombre';
			detectedRutEl.className = 'detected-rut invalid';
			fileIcon.className = 'bi bi-file-earmark-x file-icon';
			fileIcon.style.color = '#dc3545';
			uploadBtn.disabled = true;
		}

		// Show preview, hide drop zone
		dropZone.style.display = 'none';
		filePreview.classList.add('show');
	}

	// Remove selected file
	function removeFile() {
		selectedFile = null;
		detectedRut = null;
		detectedTaxpayer = null;
		fileInput.value = '';
		filePreview.classList.remove('show');
		dropZone.style.display = 'flex';
		uploadBtn.disabled = false;
	}

	// Upload file
	function uploadFile() {
		if (!selectedFile) {
			alert('Por favor seleccione un archivo');
			return;
		}

		if (!detectedTaxpayer) {
			alert('No se pudo identificar el cliente. El nombre del archivo debe contener un RUT v\u00e1lido.');
			return;
		}

		uploadBtn.disabled = true;
		uploadBtn.innerHTML = '<i class="bi bi-hourglass-split"></i>';

		var formData = new FormData();
		formData.append('fileUpload', selectedFile);

		fetch(uploadUrl, {
			method: 'POST',
			headers: { 'Accept': 'application/json' },
			body: formData
		})
		.then(function(response) { return response.json(); })
		.then(function(result) {
			if (result.success) {
				removeFile();
				onSuccess(result);
			} else {
				alert('Error en la importaci\u00f3n:\n\n' + (result.errorMessage || 'Error desconocido'));
			}
		})
		.catch(function(error) {
			console.error('Upload error:', error);
			alert('Error de conexi\u00f3n al procesar el archivo:\n\n' + error.message);
		})
		.finally(function() {
			uploadBtn.disabled = false;
			uploadBtn.innerHTML = '<i class="bi bi-upload"></i>';
		});
	}

	// Event listeners
	dropZone.addEventListener('click', function() { fileInput.click(); });

	dropZone.addEventListener('dragover', function(e) {
		e.preventDefault();
		dropZone.classList.add('drag-over');
	});

	dropZone.addEventListener('dragleave', function(e) {
		e.preventDefault();
		dropZone.classList.remove('drag-over');
	});

	dropZone.addEventListener('drop', function(e) {
		e.preventDefault();
		dropZone.classList.remove('drag-over');
		var files = e.dataTransfer.files;
		if (files.length > 0) {
			handleFile(files[0]);
		}
	});

	fileInput.addEventListener('change', function(e) {
		var files = e.target.files;
		if (files.length > 0) {
			handleFile(files[0]);
		}
	});

	uploadBtn.addEventListener('click', uploadFile);
	removeBtn.addEventListener('click', removeFile);
}
