# Coffee Shop Backend Golang

## I. User APIs

### 1. Create employee account

**Endpoint:** [103.166.182.58/users](103.166.182.58/users)
**METHOD:** `POST`

#### a. Request example

```json
{
  "email": "quoc.cantavil@coffee.shop",
  "name": "Quoc",
  "role": "staff",
  "staffOf": "Cantavil"
}
```

> `role` is one of [`staff`, `admin`, `superadmin`]. If `role` = `staff` must have `staffOf`

#### b. Response example

Status `201`:

```json
{
  "email": "quoc.can@coffee.shop",
  "message": "Created staff account",
  "password": "MLE#7!ha",
  "uid": "h4Zo6uSGHweE559xeYBzZxqUeWf1"
}
```

Status `400`:

```json
{
  "Code": "email-already-exists",
  "String": "http error status: 400; body: {\n  \"error\": {\n    \"code\": 400,\n    \"message\": \"EMAIL_EXISTS\",\n    \"errors\": [\n      {\n        \"message\": \"EMAIL_EXISTS\",\n        \"domain\": \"global\",\n        \"reason\": \"invalid\"\n      }\n    ]\n  }\n}\n"
}
```

Status `400`:

```json
{
  "message": "Invalid request body, missing staffOf field"
}
```

## II. Order APIs

### 1. Create Order

**Endpoint:** [http://103.166.182.58/orders](http://103.166.182.58/orders)
**METHOD:** `POST`

#### a. Pickup Order

**Example body:**

```json
{
  "user": "bPwfuthze4fKRST0G5kyZpS2iet2",
  "store": "Cantavil",
  "promo": "DISCOUNT20",
  "dateOrder": "2023-07-13T15:30:00Z",
  "orderedFoods": [
    {
      "id": "BacSiu",
      "note": "Extra_cheese",
      "quantity": 2,
      "toppingIdList": [
        "KemPhoMaiMacchiato",
        "ThachCaPhe"
      ],
      "size": "S"
    },
    {
      "id": "CaPheDenDa",
      "note": "No_onions",
      "quantity": 1,
      "toppingIdList": [
        "TraiNhan"
      ],
      "size": "M"
    }
  ],
  "pickupTime": "2023-07-13T16:30:00Z"
}
```

#### b. Delivery Order

**Example body:**

```json
{
  "user": "bPwfuthze4fKRST0G5kyZpS2iet2",
  "store": "Cantavil",
  "promo": "DISCOUNT20",
  "dateOrder": "2023-07-13T15:30:00Z",
  "orderedFoods": [
    {
      "id": "BacSiu",
      "note": "Extra_cheese",
      "quantity": 2,
      "toppingIdList": [
        "KemPhoMaiMacchiato",
        "ThachCaPhe"
      ],
      "size": "S"
    },
    {
      "id": "CaPheDenDa",
      "note": "No_onions",
      "quantity": 1,
      "toppingIdList": [
        "TraiNhan"
      ],
      "size": "M"
    }
  ],
  "address": {
    "addressNote": "End of the road",
    "formattedAddress": "14 Cộng Hoà, Tân Bình, Thành phố Hồ Chí Minh",
    "lat": 10.814586948385688,
    "lng": 106.66139390319586,
    "nameReceiver": "Nguyen Nam",
    "phone": "0987654321"
  }
}
```

#### c. Response example

Status `201`:

```json
{
  "orderID": "Z2E7JRK5tOJLDzcefsv3"
}
```

Status `400`:

```json
{
  "message": "Invalid request body"
}
```

### 2. Update Order State

**Endpoint:** [http://103.166.182.58/orders/:orderId](http://103.166.182.58/orders/:id)
**METHOD:** `PUT`

#### a. Request example

Endpoint: `http://103.166.182.58/orders/ALC3qGCD3OpG9EzYBqaF`

```json
{
  "status": "Đang xử lí"
}
```

`status` must be one of these value:

```go
const ORDER_PROCESSING = "Đang xử lí"
const ORDER_DELIVERING = "Đang giao"
const ORDER_DELIVERED = "Đã giao"
const ORDER_DELIVERY_FAILED = "Giao hàng thất bại"
const ORDER_READY = "Đã chuẩn bị"
const ORDER_COMPLETE = "Hoàn thành"
const ORDER_CANCELLED = "Đã hủy"
```

#### b. Response example

Status `200`:

```json
{
  "orderId": "ALC3qGCD3OpG9EzYBqaF",
  "status": "Đang xử lí"
}
```

Status `400`:

```json
{
  "error": "Invalid order ID",
  "orderId": "kgVuVtWJcVTcGph1tJwY"
}
```
