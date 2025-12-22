
// Cart state
let cart = [];
const cashierId = document.getElementById('cashierId').value;

// DOM Elements
const cartItemsContainer = document.getElementById('cartItems');
const emptyCartMessage = document.getElementById('emptyCartMessage');
const cartSubtotalEl = document.getElementById('cartSubtotal');
const cartTotalEl = document.getElementById('cartTotal');
const checkoutBtn = document.getElementById('checkoutBtn');
const clearBtn = document.getElementById('clearBtn');
const searchInput = document.getElementById('searchInput');
const categoryFilter = document.getElementById('categoryFilter');

// Filter & Search Logic
function filterProducts() {
    const searchTerm = searchInput.value.toLowerCase();
    const categoryId = categoryFilter.value;
    const products = document.querySelectorAll('.product-item');

    products.forEach(item => {
        const name = item.dataset.name.toLowerCase();
        const cat = item.dataset.category;

        const matchesSearch = name.includes(searchTerm);
        const matchesCategory = categoryId === 'all' || cat === categoryId;

        if (matchesSearch && matchesCategory) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

searchInput.addEventListener('input', filterProducts);
categoryFilter.addEventListener('change', filterProducts);

// Cart Logic
function addToCart(element) {
    // Traverse up to find the .product-item container to ensure we get the correct dataset
    const productItem = element.closest('.product-item');

    if (!productItem) {
        console.error("Could not find product item container");
        return;
    }

    const id = parseInt(productItem.dataset.id);
    const name = productItem.dataset.name;
    const price = parseFloat(productItem.dataset.price);

    const existingItem = cart.find(item => item.productId === id);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            productId: id,
            name: name,
            price: price,
            quantity: 1
        });
    }

    updateCartUI();
}

function updateQuantity(productId, change) {
    const item = cart.find(i => i.productId === productId);
    if (!item) return;

    item.quantity += change;

    if (item.quantity <= 0) {
        cart = cart.filter(i => i.productId !== productId);
    }

    updateCartUI();
}

function removeFromCart(productId) {
    cart = cart.filter(i => i.productId !== productId);
    updateCartUI();
}

function clearCart() {
    if (confirm('Are you sure you want to clear the cart?')) {
        cart = [];
        updateCartUI();
    }
}

function updateCartUI() {
    cartItemsContainer.innerHTML = '';

    if (cart.length === 0) {
        emptyCartMessage.style.display = 'block';
        checkoutBtn.disabled = true;
        clearBtn.disabled = true;
        cartSubtotalEl.textContent = 'Rp 0';
        cartTotalEl.textContent = 'Rp 0';
        return;
    }

    emptyCartMessage.style.display = 'none';
    checkoutBtn.disabled = false;
    clearBtn.disabled = false;

    let total = 0;

    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        const itemEl = document.createElement('div');
        itemEl.className = 'card mb-2 border-0 shadow-sm';
        itemEl.innerHTML = `
            <div class="card-body p-2 d-flex justify-content-between align-items-center">
                <div class="flex-grow-1">
                    <h6 class="mb-0 text-truncate" style="max-width: 150px;">${item.name}</h6>
                    <small class="text-muted">Rp ${item.price.toLocaleString('id-ID')}</small>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-secondary" onclick="updateQuantity(${item.productId}, -1)">-</button>
                        <button class="btn btn-outline-secondary" disabled>${item.quantity}</button>
                        <button class="btn btn-outline-primary" onclick="updateQuantity(${item.productId}, 1)">+</button>
                    </div>
                    <div class="text-end" style="min-width: 60px;">
                        <span class="fw-bold d-block">Rp ${itemTotal.toLocaleString('id-ID')}</span>
                    </div>
                    <button class="btn btn-sm btn-link text-danger" onclick="removeFromCart(${item.productId})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        `;
        cartItemsContainer.appendChild(itemEl);
    });

    cartSubtotalEl.textContent = `Rp ${total.toLocaleString('id-ID')}`;
    cartTotalEl.textContent = `Rp ${total.toLocaleString('id-ID')}`;
}

// Checkout Logic
async function checkout() {
    if (cart.length === 0) return;
    if (!cashierId) {
        alert("Session invalid. Please refresh or relogin.");
        return;
    }

    checkoutBtn.disabled = true;
    checkoutBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Processing...';

    // payload format: List<TransactionItemRequest>
    const payload = cart.map(item => ({
        productId: item.productId,
        quantity: item.quantity
    }));

    try {
        const response = await fetch(`/api/transactions?cashierId=${cashierId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Add Authorization header if using JWT in separate header, 
                // but for now we rely on session/cookie or simple request
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const transaction = await response.json();
            showReceipt(transaction);
            cart = [];
            updateCartUI();
        } else {
            const err = await response.text();
            alert('Transaction failed: ' + (err || response.statusText));
        }
    } catch (error) {
        console.error('Checkout error:', error);
        alert('An error occurred during checkout.');
    } finally {
        checkoutBtn.disabled = false;
        checkoutBtn.innerHTML = '<i class="bi bi-receipt me-2"></i> Convert to Order';
    }
}

// Receipt Logic
function showReceipt(transaction) {
    const modal = new bootstrap.Modal(document.getElementById('receiptModal'));
    const body = document.getElementById('receiptBody');

    // Build Receipt HTML
    let itemsHtml = '';
    transaction.items.forEach(item => {
        itemsHtml += `
            <div class="d-flex justify-content-between mb-1">
                <span>${item.product.name} x${item.quantity}</span>
                <span>Rp ${item.subtotal.toLocaleString('id-ID')}</span>
            </div>
        `;
    });

    body.innerHTML = `
        <div class="text-center mb-3">
            <h5 class="fw-bold">TerraCafe</h5>
            <p class="mb-0">Jalan Raya Tlogomas No. 246</p>
            <small>${new Date(transaction.transactionDate).toLocaleString('id-ID')}</small>
        </div>
        <hr class="border-secondary border-dashed">
        <div class="mb-3">
            <small class="d-block">Order ID: <strong>${transaction.transactionNumber}</strong></small>
            <small class="d-block">Cashier: ${transaction.cashier ? transaction.cashier.username : 'Unknown'}</small>
        </div>
        <hr>
        <div class="mb-3">
            ${itemsHtml}
        </div>
        <hr>
        <div class="d-flex justify-content-between fw-bold">
            <span>TOTAL</span>
            <span>Rp ${transaction.totalAmount.toLocaleString('id-ID')}</span>
        </div>
        <hr class="border-secondary border-dashed">
        <div class="text-center mt-3">
            <small>Thank you for your visit!</small>
        </div>
    `;

    modal.show();
}

function printReceipt() {
    const receiptContent = document.getElementById('receiptBody').innerHTML;
    const printWindow = window.open('', '', 'height=600,width=400');
    printWindow.document.write('<html><head><title>Print Receipt</title>');
    printWindow.document.write('<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">');
    printWindow.document.write('</head><body><div class="p-3">');
    printWindow.document.write(receiptContent);
    printWindow.document.write('</div></body></html>');
    printWindow.document.close();
    printWindow.print();
}
