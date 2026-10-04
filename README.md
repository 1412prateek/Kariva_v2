# Kariva — Luxury Jewelry Commerce Platform

Kariva is an exclusive one-to-many e-commerce mobile application and multiplatform ecosystem built for independent luxury jewelry creators and their discerning shoppers.

## 💎 Core Capabilities

- **Curated Creator Catalog**: Single creator uploads and curates fine jewelry collections (Rings, Necklaces, Earrings, Bracelets, Sets).
- **Role-Based Access Control (RBAC)**:
  - **Creator/Admin**: Secure access (`shikha@kariva.com`), product CRUD management, description and tier pricing editing, stock level tracking, low-stock threshold alerts, fulfillment status automation (Pending -> Processing -> Shipped -> Delivered), sales analytics & real-time revenue dashboard.
  - **Shopper/Customer**: Seamless luxury browsing, filter & search, interactive image galleries, pricing tier selector, bag/cart management, wishlist, simulated payment checkout, real-time visual timeline order tracking, loyalty membership status.
- **Real-Time Inventory Engine**: Stock levels decrement upon order, live indicators for "Low Stock" and "In Stock", automated backorder warnings.
- **Firebase Firestore & Auth Integration**: Structured NoSQL collections for `products`, `orders`, and `users` backed by Firebase Authentication with role enforcement.
- **Native iOS Luxury Aesthetic**:
  - Warm champagne & rich charcoal palettes, subtle frosted blur cards, elegant serif typography, iOS pill action buttons, fluid page transitions, and smooth micro-interactions.
- **Responsive & Multiplatform Ready**: Fluid layouts adapting across mobile, foldables, tablets, and desktop displays with centered max-width constraint architecture.

## 🚀 GitHub & CI/CD Setup

### Remote Repository
```bash
git remote add origin https://github.com/1412prateek/Kariva.git
git branch -M main
```

### Branch Protection Configuration
To configure branch protection rules on GitHub as required:
1. Navigate to your repository on GitHub: `https://github.com/1412prateek/Kariva`
2. Go to **Settings** > **Branches**.
3. Under **Branch protection rules**, click **Add rule**.
4. Set **Branch name pattern** to `main`.
5. Check **Require a pull request before merging** and configure:
   - **Require approvals**: Check (1 or more reviews required).
   - **Dismiss stale pull request approvals when new commits are pushed**.
6. Check **Require status checks to pass before merging** and choose `build-and-test`.
7. Click **Create** / **Save changes**.

### CI/CD Workflow
Automated on every push and pull request to `main` via `.github/workflows/ci.yml`:
- Environment setup (JDK 17 + Gradle caching).
- Automated test execution (`testDebugUnitTest`).
- Debug APK generation & artifact archiving.

## 🔑 Authentication Roles
- **Shopper**: Create account or login with any email/password.
- **Creator / Admin**: Dedicated creator login toggle in top-right corner.
  - Email: `shikha@kariva.com`
  - Password: `Shikha@1810`
