// Base URL for API calls - update this to match your server
const API_URL = '/api';

// Bootstrap Modal instance
let errorCodeModal;

// Current state
let selectedProductId = null;
let selectedVersionId = null;
let allErrorCodes = [];
let allVersions = [];
let currentPage = 1;
const itemsPerPage = 10;
let filteredErrorCodes = [];
let searchCurrentPage = 1;
const searchItemsPerPage = 10;
let searchResults = [];

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
    
    // Set up search functionality
    document.getElementById('searchButton').addEventListener('click', performSearch);
    document.getElementById('clearSearchButton').addEventListener('click', clearSearch);
    document.getElementById('toggleAdvanced').addEventListener('click', toggleAdvancedOptions);
    document.getElementById('searchType').addEventListener('change', updateSearchUI);
    
    // Load initial data
    loadProducts();
    loadAllVersions();

    document.getElementById('prevPage').addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            displayErrorCodesTable(filteredErrorCodes);
        }
    });
    
    document.getElementById('nextPage').addEventListener('click', () => {
        const totalPages = Math.ceil(filteredErrorCodes.length / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            displayErrorCodesTable(filteredErrorCodes);
        }
    });

    document.getElementById('searchPrevPage').addEventListener('click', () => {
        if (searchCurrentPage > 1) {
            searchCurrentPage--;
            displaySearchResults(searchResults);
        }
    });
    
    document.getElementById('searchNextPage').addEventListener('click', () => {
        const totalPages = Math.ceil(searchResults.length / searchItemsPerPage);
        if (searchCurrentPage < totalPages) {
            searchCurrentPage++;
            displaySearchResults(searchResults);
        }
    });
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
                `<tr><td colspan="7" class="text-center">Failed to load error codes</td></tr>`;
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

// Update the displayErrorCodesTable function
function displayErrorCodesTable(errorCodes, targetElement = document.getElementById('error-codes-table')) {
    filteredErrorCodes = errorCodes;
    const totalPages = Math.ceil(errorCodes.length / itemsPerPage);
    
    document.getElementById('currentPage').textContent = currentPage;
    document.getElementById('totalPages').textContent = totalPages;
    
    if (errorCodes.length === 0) {
        targetElement.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No error codes found</td></tr>';
        return;
    }
    
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const paginatedCodes = errorCodes.slice(start, end);
    
    let html = '';
    paginatedCodes.forEach((errorCode, index) => {
        const rowNumber = start + index + 1;  // Calculate continuous row number
        html += `
            <tr class="error-code-row" data-error-code-id="${errorCode.errorCodeId}">
                <td>${rowNumber}</td>
                <td>${errorCode.errorCodeId || 'N/A'}</td>
                <td>${errorCode.conditionId || 'N/A'}</td>
                <td>${errorCode.component || 'N/A'}</td>
                <td><span class="badge severity-${errorCode.severity?.toLowerCase()}">${errorCode.severity || 'N/A'}</span></td>
                <td>${errorCode.callhome || 'N/A'}</td>
                <td>${errorCode.alertName || 'N/A'}</td>
                <td>${truncateText(errorCode.description, 50) || 'N/A'}</td>
            </tr>
        `;
    });
    
    targetElement.innerHTML = html;
    
    // Update pagination buttons state
    document.getElementById('prevPage').disabled = currentPage === 1;
    document.getElementById('nextPage').disabled = currentPage === totalPages;
    
    // Add event listeners to rows
    targetElement.querySelectorAll('.error-code-row').forEach(row => {
        row.addEventListener('click', function() {
            const errorCodeId = this.getAttribute('data-error-code-id');
            showErrorCodeDetails(errorCodeId);
        });
    });
}

// Show error code details in modal
function showErrorCodeDetails(errorCodeId) {
    // First try to find in the current list
    let errorCode = allErrorCodes.find(ec => ec.errorCodeId == errorCodeId);
    
    // If not found in current list, try search results
    if (!errorCode) {
        const searchResults = document.querySelectorAll('#searchResultsList .error-code-row');
        if (searchResults.length > 0) {
            // Fetch the error code from the API
            fetch(`${API_URL}/errorCodes/findByErrorCodeId/${errorCodeId}`)
                .then(response => response.json())
                .then(data => {
                    displayErrorCodeDetails(data);
                })
                .catch(error => {
                    console.error('Error fetching error code details:', error);
                });
            return;
        }
    } else {
        displayErrorCodeDetails(errorCode);
    }
}

function displayErrorCodeDetails(errorCode) {
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
                        <td>${errorCode.conditionId || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Component</th>
                        <td>${errorCode.component || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Severity</th>
                        <td><span class="badge severity-${errorCode.severity?.toLowerCase() || 'info'}">${errorCode.severity || 'N/A'}</span></td>
                    </tr>
                    <tr>
                        <th>Call Home</th>
                        <td>${errorCode.callhome || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Alert Name</th>
                        <td>${errorCode.alertName || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Description</th>
                        <td>${errorCode.description || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Root Cause Analysis</th>
                        <td>${errorCode.rca || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Corrective Action</th>
                        <td>${errorCode.correctiveAction || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Event Source</th>
                        <td>${errorCode.eventSource || 'N/A'}</td>
                    </tr>
                    <tr>
                        <th>Alert Type</th>
                        <td>${errorCode.alertType || 'N/A'}</td>
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
    const filterValue = document.getElementById('filter-input').value;
    
    if (!filterValue || filterValue.trim() === '') {
        // If no filter, show all error codes
        displayErrorCodesTable(allErrorCodes);
        return;
    }
    
    const filteredErrorCodes = allErrorCodes.filter(errorCode => {
        // Search across multiple fields
        return (
            (errorCode.errorCodeId && errorCode.errorCodeId.toString().includes(filterValue)) ||
            (errorCode.conditionId && errorCode.conditionId.includes(filterValue)) ||
            (errorCode.component && errorCode.component.includes(filterValue)) ||
            (errorCode.severity && errorCode.severity.includes(filterValue)) ||
            (errorCode.alertName && errorCode.alertName.includes(filterValue)) ||
            (errorCode.description && errorCode.description.includes(filterValue))
        );
    });
    
    displayErrorCodesTable(filteredErrorCodes);
}

// Clear error codes panel and table
function clearErrorCodes() {
    document.getElementById('error-codes-container').innerHTML = 
        '<p class="text-muted text-center">Select a version to view its error codes</p>';
    document.getElementById('error-codes-table').innerHTML = 
        '<tr><td colspan="7" class="text-center text-muted">No error codes selected</td></tr>';
    currentPage = 1;
    filteredErrorCodes = [];
    allErrorCodes = [];
}

// Helper function to truncate text
function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}

// ===== ADVANCED SEARCH FUNCTIONS =====

// Load all versions for the filter dropdown
async function loadAllVersions() {
    try {
        const response = await fetch(`${API_URL}/versions`);
        if (!response.ok) throw new Error('Failed to load versions');
        
        allVersions = await response.json();
        populateVersionFilter();
    } catch (error) {
        console.error('Error loading versions for filter:', error);
    }
}

function populateVersionFilter() {
    const versionFilter = document.getElementById('versionFilter');
    versionFilter.innerHTML = '<option value="">All Versions</option>';
    
    if (allVersions && allVersions.length > 0) {
        allVersions.forEach(version => {
            const option = document.createElement('option');
            option.value = version.versionId;
            option.textContent = `Version ${version.versionNumber} - ${version.product ? version.product.name : 'N/A'}`;
            versionFilter.appendChild(option);
        });
    }
}

function toggleAdvancedOptions() {
    const advancedOptions = document.getElementById('advancedOptions');
    const toggle = document.getElementById('toggleAdvanced');
    
    if (advancedOptions.style.display === 'none') {
        advancedOptions.style.display = 'flex';
        toggle.textContent = 'Hide Advanced Options';
    } else {
        advancedOptions.style.display = 'none';
        toggle.textContent = 'Show Advanced Options';
    }
}

function updateSearchUI() {
    const searchType = document.getElementById('searchType').value;
    const searchQuery = document.getElementById('searchQuery');
    
    switch (searchType) {
        case 'errorCodeId':
            searchQuery.placeholder = "Enter exact Error Code ID";
            break;
        case 'condition':
            searchQuery.placeholder = "Enter condition ID (e.g. DRIVE-001)";
            break;
        case 'component':
            searchQuery.placeholder = "Enter component name (e.g. STORAGE)";
            break;
        case 'id':
            searchQuery.placeholder = "Enter ID value (e.g. ERR1001)";
            break;
        case 'version':
            searchQuery.placeholder = "Enter version ID (e.g. 1)";
            break;
        case 'severity':
            searchQuery.placeholder = "Enter severity (CRITICAL, WARNING, INFO)";
            break;
        default:
            searchQuery.placeholder = "Enter search terms...";
    }
}

async function performSearch() {
    searchCurrentPage = 1; // Reset to first page
    const searchType = document.getElementById('searchType').value;
    const searchQuery = document.getElementById('searchQuery').value.trim();
    const severityFilter = document.getElementById('severityFilter').value;
    const callhomeFilter = document.getElementById('callhomeFilter').value;
    const versionFilter = document.getElementById('versionFilter').value;
    
    // Show loading indicator in the results area
    document.getElementById('searchResultsList').innerHTML = 
        '<tr><td colspan="7" class="text-center">Searching...</td></tr>';
    
    try {
        let response;
        let errorCodes = [];
        
        // If a search query is provided in the All Error Codes mode,
        // first try to find by exact errorCodeId
        if (searchType === 'all' && searchQuery) {
            try {
                const directResponse = await fetch(`${API_URL}/errorCodes/findByErrorCodeId/${searchQuery}`);
                if (directResponse.ok) {
                    const directResult = await directResponse.json();
                    if (directResult) {
                        // If we found an exact match, use it
                        if (Array.isArray(directResult)) {
                            errorCodes = directResult;
                        } else {
                            errorCodes = [directResult];
                        }
                        
                        // Display results and return early
                        displayErrorCodesTable(errorCodes, document.getElementById('searchResultsList'));
                        showAlert('info', `Found ${errorCodes.length} error code(s) matching your search criteria`);
                        return;
                    }
                }
            } catch (directError) {
                console.log('Direct search failed, falling back to general search', directError);
                // If direct search fails, we'll continue with the general search
            }
        }
        
        // Determine which endpoint to call based on search type
        switch (searchType) {
            case 'errorCodeId':
                response = await fetch(`${API_URL}/errorCodes/findByErrorCodeId/${searchQuery}`);
                break;
            case 'condition':
                response = await fetch(`${API_URL}/errorCodes/byConditionId?conditionId=${searchQuery}`);
                break;
            case 'component':
                response = await fetch(`${API_URL}/errorCodes/byComponent?component=${searchQuery}`);
                break;
            case 'id':
                response = await fetch(`${API_URL}/errorCodes/byId?id=${searchQuery}`);
                break;
            case 'version':
                response = await fetch(`${API_URL}/errorCodes/byVersionId/${searchQuery}`);
                break;
            case 'severity':
                // There's no direct API for severity, so we get all and filter
                response = await fetch(`${API_URL}/errorCodes`);
                break;
            case 'all':
            default:
                response = await fetch(`${API_URL}/errorCodes`);
        }
        
        if (!response.ok) {
            throw new Error('Search request failed');
        }
        
        errorCodes = await response.json();
        
        // For the exact error code ID search, we may get a single object instead of an array
        if (searchType === 'errorCodeId' && !Array.isArray(errorCodes)) {
            errorCodes = [errorCodes];
        }
        
        // Apply additional filters if using the 'all' endpoint or severity filter
        if ((searchType === 'all' || searchType === 'severity') && errorCodes.length > 0) {
            // Filter by free text if provided
            if (searchQuery && searchType === 'all') {
                const lowerQuery = searchQuery;
                errorCodes = errorCodes.filter(code => 
                    code.errorCodeId.toString().includes(lowerQuery) ||
                    (code.conditionId && code.conditionId.includes(lowerQuery)) ||
                    (code.component && code.component.includes(lowerQuery)) ||
                    (code.alertName && code.alertName.includes(lowerQuery)) ||
                    (code.description && code.description.includes(lowerQuery)) ||
                    (code.severity && code.severity.includes(lowerQuery))
                );
            }
            
            // Apply severity filter
            if (severityFilter) {
                errorCodes = errorCodes.filter(code => code.severity === severityFilter);
            }
            
            // Apply callhome filter
            if (callhomeFilter) {
                errorCodes = errorCodes.filter(code => code.callhome === callhomeFilter);
            }
            
            // Apply version filter
            if (versionFilter) {
                const versionResponse = await fetch(`${API_URL}/errorCodes/byVersionId/${versionFilter}`);
                const versionErrorCodes = await versionResponse.json();
                const versionErrorCodeIds = versionErrorCodes.map(code => code.errorCodeId);
                errorCodes = errorCodes.filter(code => versionErrorCodeIds.includes(code.errorCodeId));
            }
        }
        
        // Store search results globally
        searchResults = errorCodes;
        
        // Display results with pagination
        displaySearchResults(searchResults);
        
        // Update result count
        document.getElementById('searchResultsCount').textContent = 
            `Found ${errorCodes.length} result(s)`;
            
    } catch (error) {
        console.error('Search error:', error);
        document.getElementById('searchResultsList').innerHTML = 
            '<tr><td colspan="7" class="text-center text-danger">Search failed. Please try again.</td></tr>';
        showAlert('danger', 'An error occurred during the search');
    }
}

// Update the displaySearchResults function
function displaySearchResults(results) {
    const totalPages = Math.ceil(results.length / searchItemsPerPage);
    
    document.getElementById('searchCurrentPage').textContent = searchCurrentPage;
    document.getElementById('searchTotalPages').textContent = totalPages;
    
    if (results.length === 0) {
        document.getElementById('searchResultsList').innerHTML = 
            '<tr><td colspan="8" class="text-center">No results found</td></tr>';
        return;
    }
    
    const start = (searchCurrentPage - 1) * searchItemsPerPage;
    const end = start + searchItemsPerPage;
    const paginatedResults = results.slice(start, end);
    
    let html = '';
    paginatedResults.forEach((errorCode, index) => {
        const rowNumber = start + index + 1;  // Calculate continuous row number
        html += `
            <tr class="error-code-row" data-error-code-id="${errorCode.errorCodeId}">
                <td>${rowNumber}</td>
                <td>${errorCode.errorCodeId || 'N/A'}</td>
                <td>${errorCode.conditionId || 'N/A'}</td>
                <td>${errorCode.component || 'N/A'}</td>
                <td><span class="badge severity-${errorCode.severity?.toLowerCase()}">${errorCode.severity || 'N/A'}</span></td>
                <td>${errorCode.callhome || 'N/A'}</td>
                <td>${errorCode.alertName || 'N/A'}</td>
                <td>${truncateText(errorCode.description, 50) || 'N/A'}</td>
            </tr>
        `;
    });
    
    document.getElementById('searchResultsList').innerHTML = html;
    
    // Update pagination buttons state
    document.getElementById('searchPrevPage').disabled = searchCurrentPage === 1;
    document.getElementById('searchNextPage').disabled = searchCurrentPage === totalPages;
    
    // Add click handlers for rows
    document.querySelectorAll('#searchResultsList .error-code-row').forEach(row => {
        row.addEventListener('click', () => {
            const errorCodeId = row.getAttribute('data-error-code-id');
            showErrorCodeDetails(errorCodeId);
        });
    });
}

// Update clearSearch function
function clearSearch() {
    document.getElementById('searchQuery').value = '';
    document.getElementById('severityFilter').value = '';
    document.getElementById('callhomeFilter').value = '';
    document.getElementById('versionFilter').value = '';
    document.getElementById('searchResultsList').innerHTML = 
        '<tr><td colspan="7" class="text-center">Use the search box above to find error codes</td></tr>';
    document.getElementById('searchResultsCount').textContent = '';
    searchCurrentPage = 1;
    searchResults = [];
}

function showAlert(type, message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    const container = document.querySelector('.container-fluid');
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 150);
    }, 5000);
}