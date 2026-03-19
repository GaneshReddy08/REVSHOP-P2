/**
 * RevShop - Shared JavaScript
 */

// Add a product to cart (uses query params as expected by the API)
async function addToCart(productId, quantity = 1) {
    try {
        const response = await fetch(`/api/cart/add?productId=${productId}&quantity=${quantity}`, {
            method: 'POST'
        });

        if (response.ok) {
            alert("✅ Product added to cart!");
            updateCartCount();
        } else if (response.status === 403 || response.status === 401) {
            // User is not logged in or not a BUYER
            if (confirm("Would you like to log in or create an account to start adding items to your cart?")) {
                window.location.href = '/login';
            }
        } else {
            const error = await response.text();
            alert("Error: " + error);
        }
    } catch (err) {
        alert("Request failed: " + err);
    }
}

// Update the cart count badge in the header
async function updateCartCount() {
    try {
        const badge = document.getElementById('cart-count');
        if (!badge) return; // Not logged in as buyer, no badge exists

        // We need the userId from the hidden field or session
        // The cart endpoint requires a userId in the URL, but the backend ignores it
        // and uses the authenticated user instead. We can pass 0 as a placeholder.
        const res = await fetch('/api/cart/user/0');
        if (res.ok) {
            const cart = await res.json();
            const count = cart.items ? cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
            badge.textContent = count;
        }
    } catch (e) {
        // Silently fail — user might not be logged in
    }
}

// Toggle favorite status
async function toggleFavorite(productId, element, isWishlistPage = false) {
    try {
        const response = await fetch(`/api/favorites/toggle/${productId}`, {
            method: 'POST'
        });

        if (response.ok) {
            const data = await response.json();
            if (data.isFavorited) {
                element.classList.add('favorited');
            } else {
                element.classList.remove('favorited');
                if (isWishlistPage) {
                    // Remove from view if on wishlist page
                    const card = document.getElementById(`fav-product-${productId}`);
                    if (card) {
                        card.style.opacity = '0';
                        setTimeout(() => {
                            card.remove();
                            if (document.querySelectorAll('#favoritesGrid .product-card').length === 0) {
                                document.getElementById('favoritesGrid').style.display = 'none';
                                document.getElementById('noFavorites').style.display = 'block';
                            }
                        }, 300);
                    }
                }
            }
        } else if (response.status === 401) {
            if (confirm("To save this to your wishlist, join RevShop or sign in to your account. Go to login page?")) {
                window.location.href = '/login';
            }
        } else {
            const error = await response.json();
            alert(error.message || "Error updating favorite");
        }
    } catch (err) {
        console.error("Favorite toggle failed:", err);
    }
}

// Initialize cart count on page load
document.addEventListener('DOMContentLoaded', updateCartCount);