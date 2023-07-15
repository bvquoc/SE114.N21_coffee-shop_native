package models

type FoodOrder struct {
	ID          string   `json:"id"`
	Name        string   `json:"name"`
	Image       string   `json:"image"`
	Note        string   `json:"note"`
	Quantity    int      `json:"quantity"`
	ToppingsStr string   `json:"topping"`
	ToppingIds  []string `json:"toppingIdList"`
	Size        string   `json:"size"`
	UnitPrice   int      `json:"unitPrice"`
	TotalPrice  int      `json:"totalPrice"`
}
