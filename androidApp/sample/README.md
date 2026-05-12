рас# Handoff: Кит маркет — Mobile App (Compose Multiplatform)

## Overview
Grocery delivery mobile app ("Кит маркет"). 9 screens covering auth, product catalog, cart/checkout, and profile flows. Target platform: Compose Multiplatform (KMP) — Android + iOS.

## About the Design Files
The files in this bundle are **design references created in HTML** — interactive prototypes showing intended look and behavior, not production code to copy directly. Your task is to **recreate these HTML designs as Compose Multiplatform @Composable functions** in `commonMain`, using Compose idioms (Material3 where appropriate, custom components where design diverges from Material).

## Fidelity
**High-fidelity**. Colors, spacing, typography, and corner radii are final. Recreate pixel-perfectly.

## Screens

### 1. SplashScreen
- Full-bleed blue background `#5383EC`
- Centered "Кит маркет" text: Andika Bold 36sp, white
- Auto-navigates after ~1.6s: to LoginScreen if not authed, else HomeScreen

### 2. LoginScreen
- White background
- Top 290dp: decorative header image (`veg-header.png`)
- Logo badge centered at y=260: blue pill (239×68, radius 10, bg `#5383EC`), white "Кит маркет" Andika Bold 32sp
- "Введите номер телефона" label at y=430, Andika 22sp
- Input row at y=478: Russian flag (34×22), "+7" text, input with mask `(XXX) XXX-XX-XX`
- Bottom button "Продолжить" — PrimaryButton
- On submit → HomeScreen, mark user as authed

### 3. HomeScreen (Каталог)
- Logo badge at top
- Search bar: bg `#F2F3F2`, radius 15, height 54. Placeholder "Найти продукт" with search icon + filter icon (opens FilterScreen)
- Section "Фрукты" — 2-column grid of ProductCards (Бананы 200, Яблоки красные 100)
- Section "Бестселлеры" — 2-column grid (Красный перец 500, Имбирь 250)
- Each section has "Посмотреть все" link (`#5383EC`)
- Bottom TabBar

### 4. ProductScreen
- Top 380dp: grey bg (`#F2F3F2`), centered product image 260×260, pagination dots
- Back button (circular white, top-left)
- Title (Andika 28sp), subtitle (Andika 14sp, muted)
- Stepper (−/qty/+) + total price
- Collapsible "Описание продукта"
- Bottom "Добавить в корзину" PrimaryButton

### 5. CartScreen
- Header "Корзина" centered
- List of cart rows: image 90×90, name, sub, remove X, stepper, line total
- Bottom button "Сделать заказ" with total badge — opens CheckoutSheet
- TabBar

### 6. CheckoutSheet (modal bottom sheet over cart)
- Title "Оплата" with close X
- Rows: Самовывоз, Оплата (Mastercard dots), Промокод, Стоимость
- Legal line "Оплачивая, вы соглашаетесь с правилами использования"
- Bottom button "Сделать заказ" with total badge
- On confirm → clear cart, show success, go to Profile

### 7. FilterScreen (modal)
- Header "Фильтр" with close X (top-left)
- Card area bg `#F7F7F7`, radius top 20
- "Категории" heading + checkbox list: Яйца / Лапша / Чипсы / Быстрая еда
- Checked: filled `#5383EC` square + white checkmark + blue label
- Bottom "Принять фильтр" PrimaryButton

### 8. OrdersScreen (История заказов)
- Back button (circular blue, top-left)
- "История заказов" h1 (Andika 32sp)
- List of orders: date/time + "Сумма: 900"
- First (live) order: filled PrimaryButton "Отследить заказ"
- Others: outlined button "Посмотреть детали заказа" (blue border + blue text)
- TabBar (active: Профиль)

### 9. ProfileScreen
- Avatar (70×70 circle) + name "Иван" + edit pencil (green)
- List: Заказы / Уведомления / Помощь (each with icon chip + chevron)
- Bottom "Выйти" button — grey bg, blue icon + text. On tap → un-auth, go to LoginScreen
- TabBar (active: Профиль)

## Navigation Flow (per user spec)
- Splash → Login (if !authed) or Home (if authed)
- Login → Home (after successful phone entry)
- Home → Category list → Product (or Home → Product)
- Product → back to Home/Category
- Cart: qty changes & removals update in-place
- Profile → Orders ("История заказов")
- Profile → Login ("Выйти")
- Tabs (Продукты / Корзина / Профиль) switch between Home / Cart / Profile

## Design Tokens

```kotlin
object KitColors {
    val Blue       = Color(0xFF5383EC)
    val BlueDark   = Color(0xFF425AC7)
    val Green      = Color(0xFF53B175)
    val Text       = Color(0xFF181725)
    val Muted      = Color(0xFF7C7C7C)
    val Border     = Color(0xFFE2E2E2)
    val BgGrey     = Color(0xFFF2F3F2)
    val BgLight    = Color(0xFFFCFCFC)
    val White      = Color(0xFFFFFFFF)
}

object KitShape {
    val Card           = RoundedCornerShape(18.dp)
    val Button         = RoundedCornerShape(29.dp)
    val AddPill        = RoundedCornerShape(17.dp)
    val Search         = RoundedCornerShape(15.dp)
    val LogoBadge      = RoundedCornerShape(10.dp)
    val Sheet          = RoundedCornerShape(topStart=20.dp, topEnd=20.dp)
}

object KitSize {
    val ButtonHeight   = 58.dp
    val StepperBtn     = 48.dp
    val TabBarHeight   = 84.dp
    val AddPillSize    = 45.dp
}
```

### Typography (Andika — Google Fonts)
| Role | Size | Weight |
|---|---|---|
| H1 (screen title) | 32sp | 400 |
| H2 (section) | 24sp | 400 |
| Title | 20sp | 400 |
| Body | 16sp | 400 |
| Body sm | 14sp | 400 |
| Caption | 12sp | 400 |
| Logo | 32sp | 700 |

## Spacing
- Screen side padding: 22dp
- Card padding: 14dp
- Grid gap: 14dp
- Section top: 28dp

## Components to build (reusable)
- `PrimaryButton` — 58dp height, 29dp corner, `KitColors.Blue`, Andika 18sp white
- `OutlinedButton` — same metrics, 1.5dp border `Blue`, blue text
- `Stepper` — two 48dp square buttons with 14dp corner, 1dp border Muted
- `ProductCard` — 18dp radius, 1dp border `Border`, 248dp height
- `TabBar` — 84dp, 3 items (Продукты / Корзина / Профиль), top shadow
- `LogoBadge` — blue pill with white Andika Bold text

## Assets
All in `assets/` subfolder — copy to `composeResources/drawable/`:
- `bananas.png`, `apples.png`, `pepper.png`, `ginger.png` — product photos
- `veg-header.png` — login screen header illustration
- `cat-avatar.jpg` — profile avatar

## State Management
- `authed: Boolean` — persists to settings (multiplatform-settings or DataStore)
- `cart: List<CartItem>` — persists; items: `{id, name, sub, price, img, qty}`
- Current route — use Voyager or androidx.navigation.compose

## Suggested structure
```
commonMain/kotlin/<pkg>/
├── ui/
│   ├── theme/          KitColors, KitShape, KitTypography
│   ├── components/     PrimaryButton, Stepper, ProductCard, TabBar, LogoBadge
│   └── screens/        Splash, Login, Home, Product, Cart, Checkout, Filter, Orders, Profile
├── data/
│   └── Product.kt, CartItem.kt
└── App.kt              Nav host + state holder
```

## Files in this handoff
- `Kit Market.html` — the interactive prototype (open in a browser to see every state)
- `assets/` — all images referenced by the design
- `README.md` — this file
