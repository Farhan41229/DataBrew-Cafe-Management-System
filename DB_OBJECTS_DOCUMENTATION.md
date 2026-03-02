# DataBrew Cafe — Database Objects Documentation

> **Database:** `cafedb` (MySQL 8.0+, utf8mb4)

---

## Functions

| # | Name | Parameters | Returns | Description | File |
|---|------|-----------|---------|-------------|------|
| 1 | `calculate_tax` | `p_amount DECIMAL`, `p_tax_id BIGINT` | `DECIMAL(10,2)` | Looks up tax rate by ID and returns `amount × rate / 100`. Returns 0 if tax not found. | `schema.sql:193` |
| 2 | `calculate_discount` | `p_amount DECIMAL`, `p_discount_id BIGINT`, `p_customer_type ENUM` | `DECIMAL(10,2)` | Resolves discount by ID; applies PERCENT or FLAT value. Returns 0 if discount doesn't exist or customer type doesn't match. | `schema.sql:201` |
| 3 | `get_total_order_price` | `p_order_id BIGINT` | `DECIMAL(10,2)` | Sums `line_total` from `order_items` for the given order. Returns 0 if no items. | `schema.sql:214` |

---

## Stored Procedures

| # | Name | Parameters | Description | File |
|---|------|-----------|-------------|------|
| 1 | `create_order_procedure` | `p_customer_name`, `p_customer_type`, `p_tax_id`, `p_discount_id` | Calculates subtotal, discount, tax, and total for the last-inserted order using the three functions above, then updates the `orders` row. | `schema.sql:226` |
| 2 | `generate_invoice_procedure` | `p_order_id`, `p_payment_id` | Generates an invoice number (`INV-YYYYMMDD-000XXX`), reads order total, and inserts into `invoices`. | `schema.sql:253` |
| 3 | `daily_sales_report_procedure` | `p_date DATE` | Returns a single-row result: order count, items sold, and revenue for the given date (excludes cancelled). | `schema.sql:262` |
| 4 | `record_purchase_procedure` | `p_supplier_id`, `p_ingredient_id`, `p_quantity`, `p_cost` | Inserts a purchase record and increases the matching inventory quantity. | `extend_schema.sql:98` |
| 5 | `cancel_order_procedure` | `p_order_id` | Validates order isn't already cancelled, reverses inventory deductions for all order items, sets status to `CANCELLED`, and writes an audit log. | `extend_schema.sql:116` |
| 6 | `range_sales_report_procedure` | `p_from DATE`, `p_to DATE` | Returns daily breakdown (date, orders, items sold, revenue) for a date range, excluding cancelled orders. | `extend_schema.sql:143` |
| 7 | `monthly_summary_procedure` | `p_year INT`, `p_month INT` | Returns aggregated monthly stats: total orders, items sold, revenue, avg order value, and cancelled count. | `extend_schema.sql:162` |
| 8 | `create_index_if_not_exists` | *(none)* | Utility procedure that conditionally creates performance indexes on `audit_logs` and `orders`. Dropped immediately after execution. | `extend_schema.sql:202` |

---

## Triggers

| # | Name | Timing | Table | Description | File |
|---|------|--------|-------|-------------|------|
| 1 | `trg_users_before_insert` | BEFORE INSERT | `users` | Validates that `password_hash` is ≥ 20 chars (rejects weak hashes). Logs a `CREATE_USER` entry in `audit_logs`. | `schema.sql:276` |
| 2 | `trg_order_items_after_insert` | AFTER INSERT | `order_items` | Deducts inventory for each ingredient linked to the ordered menu item via the `menu_item_ingredients` junction table (`qty_needed × order quantity`). Only deducts if sufficient stock exists. | `fix_trigger.sql:82` *(fixed version)* |
| 3 | `trg_payments_after_insert` | AFTER INSERT | `payments` | Sets the parent order's status to `PAID` and writes a `PAYMENT` audit log entry. | `schema.sql:298`, `restore_trigger.sql:3` |
| 4 | `trg_inventory_after_update` | AFTER UPDATE | `inventory` | Logs every inventory quantity change to `audit_logs` with old → new values. | `extend_schema.sql:184` |

> **Note:** The original `trg_order_items_after_insert` in `schema.sql:287` had a broken JOIN that deducted from ALL inventory rows. The corrected version in `fix_trigger.sql` uses the `menu_item_ingredients` junction table for proper per-ingredient deduction.

---

## Views

| # | Name | Description | File |
|---|------|-------------|------|
| 1 | `pos_menu_view` | Active menu items joined with category names, sorted by category → item name. Used by the POS screen. | `extend_schema.sql:9` |
| 2 | `dashboard_stats_view` | Single-row today's snapshot: orders count, revenue, avg order value, pending orders, active employees, low-stock items. | `extend_schema.sql:19` |
| 3 | `top_selling_items_view` | Items ranked by total quantity sold, with revenue and distinct order count (excludes cancelled). | `extend_schema.sql:31` |
| 4 | `supplier_summary_view` | Each supplier with purchase count, total spent, and last purchase date. | `extend_schema.sql:44` |
| 5 | `revenue_by_category_view` | Revenue and items sold aggregated by menu category (excludes cancelled). | `extend_schema.sql:55` |
| 6 | `payment_method_breakdown_view` | Payment count and total amount grouped by method (CASH / CARD / MFS). | `extend_schema.sql:65` |
| 7 | `order_history_view` | Full order history with discount name, tax name, payment method, and invoice number joined in. | `extend_schema.sql:75` |

---

## Supporting Table

| Name | Description | File |
|------|-------------|------|
| `menu_item_ingredients` | Junction table mapping each `menu_item` to its `ingredient`(s) with `qty_needed` per unit sold. Required by the fixed inventory trigger. | `fix_trigger.sql:4` |

---

*All file paths are relative to `resources/`.*
