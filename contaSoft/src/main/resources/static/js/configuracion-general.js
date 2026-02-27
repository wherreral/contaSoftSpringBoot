// JS helpers for admin: configuracion-general
function loadGeneralConfiguration() {
    const token = localStorage.getItem('jwtToken') || localStorage.getItem('token');
    fetch('/api/ui/configuracion-general', {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': token ? ('Bearer ' + token) : ''
        }
    }).then(r => r.json()).then(resp => {
        if (resp && resp.success && Array.isArray(resp.data)) {
            const conf = resp.data.find(c => c.name === 'habilitar_sign_in');
            if (conf) {
                const val = (conf.value === 'true' || conf.value === '1');
                document.querySelectorAll('[name="habilitar_sign_in"]').forEach(el => {
                    if (el.value === 'true') el.checked = val;
                    if (el.value === 'false') el.checked = !val;
                });
                // store id for updates
                document.getElementById('habilitar_sign_in').dataset.confId = conf.id;
            }
        }
    }).catch(err => console.error('Error cargando configuracion general', err));
}

function saveHabilitarSignIn(value) {
    const token = localStorage.getItem('jwtToken') || localStorage.getItem('token');
    const el = document.getElementById('habilitar_sign_in');
    const id = el ? el.dataset.confId : null;
    const body = { value: value ? 'true' : 'false', description: 'Habilitar Sign In', name: 'habilitar_sign_in' };
    if (!id) {
        // Create new configuration
        fetch('/api/ui/configuracion-general', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': token ? ('Bearer ' + token) : ''
            },
            body: JSON.stringify(body)
        }).then(r => r.json()).then(resp => {
            if (resp && resp.success) {
                // After creation/update, reload configuration list to obtain id
                setTimeout(loadGeneralConfiguration, 200);
                console.log('Configuración creada/actualizada');
            } else {
                alert('Error al crear configuración: ' + (resp.message || JSON.stringify(resp)));
            }
        }).catch(err => {
            console.error('Error creando configuración', err);
            alert('Error creando configuración');
        });
    } else {
        // Update existing
        fetch('/api/ui/configuracion-general/' + id, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': token ? ('Bearer ' + token) : ''
            },
            body: JSON.stringify(body)
        }).then(r => r.json()).then(resp => {
            if (resp && resp.success) {
                console.log('Configuración actualizada');
            } else {
                alert('Error al guardar: ' + (resp.message || JSON.stringify(resp)));
            }
        }).catch(err => {
            console.error('Error guardando configuración', err);
            alert('Error guardando configuración');
        });
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Attach listeners
    document.querySelectorAll('[name="habilitar_sign_in"]').forEach(rb => {
        rb.addEventListener('change', function(e) {
            saveHabilitarSignIn(this.value === 'true');
        });
    });
    loadGeneralConfiguration();
});
