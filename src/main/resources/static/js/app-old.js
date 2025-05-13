// Base URL for API calls - update this to match your server
const API_URL = '/api';

// Bootstrap Modal instance
let errorCodeModal;

// Current state
let selectedProductId = null;
let selectedVersionId = null;
let allErrorCodes = [];

// Document ready function
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap Modal
    errorCodeModal = new bootstrap.Modal(document.getElementById('errorCodeModal'));
    
    // Set up event listeners
    document.getElementById('filter-button').addEventListener('click', filterErrorCodes);
    document.getElementById('filter-input').addEventListener('keyup', function(event) {
        if (event.key === 'Enter') {
            filterErrorCodes();
        }
    });
    
    // Load initial data
    loadProducts();
});

// Load products from API
function loadProducts() {
    fetch(`${API_URL}/products`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(products => {
            displayProducts(products);
        })
        .catch(error => {
            console.error('Error loading products:', error);
            document.getElementById('product-list').innerHTML = 
                `<div class="alert alert-danger">Failed to load products: ${error.message}</div>`;
        });
}

// Display products in the list
function displayProducts(products) {
    const productList = document.getElementById('product-list');
    
    if (products.length === 0) {
        productList.innerHTML = '<p class="text-muted text-center">No products found</p>';
        return;
    }
    
    let html = '';
    products.forEach(product => {
        html += `
            <a href="#" class="list-group-item list-group-item-action product-item" data-product-id="${product.productId}">
                ${product.name}
            </a>
        `;
    });
    
    productList.innerHTML = html;
    
    // Add event listeners to product items
    document.querySelectorAll('.product-item').forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all products
            document.querySelectorAll('.product-item').forEach(p => p.classList.remove('active'));
            
            // Add active class to selected product
            this.classList.add('active');
            
            // Set selected product id
            selectedProductId = this.getAttribute('data-product-id');
            
            // Load versions for selected product
            loadVersions(selectedProductId);
            
            // Clear versions and error codes
            clearErrorCodes();
        });
    });
}

// Load versions for a product
function loadVersions(productId) {
    const versionsContainer = document.getElementById('versions-container');
    versionsContainer.innerHTML = `
        <div class="d-flex justify-content-center">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    
    fetch(`${API_URL}/versions/product/${productId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(versions => {
            displayVersions(versions);
        })
        .catch(error => {
            console.error('Error loading versions:', error);
            versionsContainer.innerHTML = 
                `<div class="alert alert-danger">Failed to load versions: ${error.message}</div>`;
        });
}

// Display versions in the list
function displayVersions(versions) {
    const versionsContainer = document.getElementById('versions-container');
    
    if (versions.length === 0) {
        versionsContainer.innerHTML = '<p class="text-muted text-center">No versions found for this product</p>';
        return;
    }
    
    let html = '<div class="list-group">';
    versions.forEach(version => {
        html += `
            <a href="#" class="list-group-item list-group-item-action version-item" data-version-id="${version.versionId}">
                Version ${version.versionNumber}
            </a>
        `;
    });
    html += '</div>';
    
    versionsContainer.innerHTML = html;
    
    // Add event listeners to version items
    document.querySelectorAll('.version-item').forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all versions
            document.querySelectorAll('.version-item').forEach(v => v.classList.remove('active'));
            
            // Add active class to selected version
            this.classList.add('active');
            
            // Set selected version id
            selectedVersionId = this.getAttribute('data-version-id');
            
            // Load error codes for selected version
            loadErrorCodes(selectedVersionId);
        });
    });
}

// Load error codes for a version
function loadErrorCodes(versionId) {
    const errorCodesContainer = document.getElementById('error-codes-container');
    errorCodesContainer.innerHTML = `
        <div class="d-flex justify-content-center">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    
    fetch(`${API_URL}/errorCodes/byVersionId/${versionId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(errorCodes => {
            allErrorCodes = errorCodes; // Store all error codes for filtering
            displayErrorCodes(errorCodes);
            displayErrorCodesTable(errorCodes);
        })
        .catch(error => {
            console.error('Error loading error codes:', error);
            errorCodesContainer.innerHTML = 
                `<div class="alert alert-danger">Failed to load error codes: ${error.message}</div>`;
            document.getElementById('error-codes-table').innerHTML = 
                `<tr><td colspan="8" class="text-center">Failed to load error codes</td></tr>`;
        });
}

// Display error codes count
function displayErrorCodes(errorCodes) {
    const errorCodesContainer = document.getElementById('error-codes-container');
    
    if (errorCodes.length === 0) {
        errorCodesContainer.innerHTML = '<p class="text-muted text-center">No error codes found for this version</p>';
        return;
    }
    
    errorCodesContainer.innerHTML = `
        <div class="alert alert-info">
            <h6 class="mb-0">Found ${errorCodes.length} error code(s)</h6>
        </div>
    `;
}

// Display error codes in the table
function displayErrorCodesTable(errorCodes) {
    const errorCodesTable = document.getElementById('error-codes-table');
    
    if (errorCodes.length === 0) {
        errorCodesTable.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No error codes found</td></tr>';
        return;
    }
    
    let html = '';
    errorCodes.forEach(errorCode => {
        html += `
            <tr class="error-code-row" data-error-code-id="${errorCode.errorCodeId}">
                <td>${errorCode.errorCodeId}</td>
                <td>${errorCode.conditionId}</td>
                <td>${errorCode.component}</td>
                <td>${errorCode.idValue}</td>
                <td><span class="badge severity-${errorCode.severity.toLowerCase()}">${errorCode.severity}</span></td>
                <td>${errorCode.callhome}</td>
                <td>${errorCode.alertName}</td>
                <td>${truncateText(errorCode.description, 50)}</td>
            </tr>
        `;
    });
    
    errorCodesTable.innerHTML = html;
    
    // Add event listeners to error code rows
    document.querySelectorAll('.error-code-row').forEach(row => {
        row.addEventListener('click', function() {
            const errorCodeId = this.getAttribute('data-error-code-id');
            showErrorCodeDetails(errorCodeId);
        });
    });
}

// Show error code details in modal
function showErrorCodeDetails(errorCodeId) {
    const errorCode = allErrorCodes.find(ec => ec.errorCodeId == errorCodeId);
    
    if (!errorCode) return;
    
    const modalContent = document.getElementById('errorcode-detail-content');
    
    let html = `
        <div class="table-responsive">
            <table class="table table-bordered">
                <tbody>
                    <tr>
                        <th width="30%">Error Code ID</th>
                        <td>${errorCode.errorCodeId}</td>
                    </tr>
                    <tr>
                        <th>Condition ID</th>
                        <td>${errorCode.conditionId}</td>
                    </tr>
                    <tr>
                        <th>Component</th>
                        <td>${errorCode.component}</td>
                    </tr>
                    <tr>
                        <th>ID</th>
                        <td>${errorCode.idValue}</td>
                    </tr>
                    <tr>
                        <th>Severity</th>
                        <td><span class="badge severity-${errorCode.severity.toLowerCase()}">${errorCode.severity}</span></td>
                    </tr>
                    <tr>
                        <th>Call Home</th>
                        <td>${errorCode.callhome}</td>
                    </tr>
                    <tr>
                        <th>Alert Name</th>
                        <td>${errorCode.alertName}</td>
                    </tr>
                    <tr>
                        <th>Description</th>
                        <td>${errorCode.description}</td>
                    </tr>
                    <tr>
                        <th>Root Cause Analysis</th>
                        <td>${errorCode.rca}</td>
                    </tr>
                    <tr>
                        <th>Corrective Action</th>
                        <td>${errorCode.correctiveAction}</td>
                    </tr>
                    <tr>
                        <th>Event Source</th>
                        <td>${errorCode.eventSource}</td>
                    </tr>
                    <tr>
                        <th>Alert Type</th>
                        <td>${errorCode.alertType}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    `;
    
    modalContent.innerHTML = html;
    errorCodeModal.show();
}

// Filter error codes based on search input
function filterErrorCodes() {
    const filterValue = document.getElementById('filter-input').value.toLowerCase();
    
    if (!filterValue || filterValue.trim() === '') {
        // If no filter, show all error codes
        displayErrorCodesTable(allErrorCodes);
        return;
    }
    
    const filteredErrorCodes = allErrorCodes.filter(errorCode => {
        return (
            (errorCode.conditionId && errorCode.conditionId.toLowerCase().includes(filterValue)) ||
            (errorCode.component && errorCode.component.toLowerCase().includes(filterValue)) ||
            (errorCode.idValue && errorCode.idValue.toLowerCase().includes(filterValue))
        );
    });
    
    displayErrorCodesTable(filteredErrorCodes);
}

// Clear error codes panel and table
function clearErrorCodes() {
    document.getElementById('error-codes-container').innerHTML = 
        '<p class="text-muted text-center">Select a version to view its error codes</p>';
    document.getElementById('error-codes-table').innerHTML = 
        '<tr><td colspan="8" class="text-center text-muted">No error codes selected</td></tr>';
    allErrorCodes = [];
}

// Helper function to truncate text
function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}