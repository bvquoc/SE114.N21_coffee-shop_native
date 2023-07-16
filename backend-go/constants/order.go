package constants

const ORDER_PROCESSING = "Đang xử lí"
const ORDER_DELIVERING = "Đang giao"
const ORDER_DELIVERED = "Đã giao"
const ORDER_DELIVERY_FAILED = "Giao hàng thất bại"
const ORDER_READY = "Đã chuẩn bị"
const ORDER_COMPLETE = "Hoàn thành"
const ORDER_CANCELLED = "Đã hủy"
const ORDER_CREATED = "Đã tạo"
const SHIPPING_FEE_PER_KM = 3000
const SHIPPING_FIRST_DISTANCE = 10
const SHIPPING_FIRST_DISTANCE_FEE = 15000
const SHIPPING_MAX_FEE = 50000

func IsOrderStatus(value string) bool {
	orderStatuses := []string{
		ORDER_PROCESSING,
		ORDER_DELIVERING,
		ORDER_DELIVERED,
		ORDER_DELIVERY_FAILED,
		ORDER_READY,
		ORDER_COMPLETE,
		ORDER_CANCELLED,
		ORDER_CREATED,
	}

	for _, status := range orderStatuses {
		if value == status {
			return true
		}
	}

	return false
}
