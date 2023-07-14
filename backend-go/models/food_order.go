package models

type FoodOrder struct {
	ID          string   `json:"id"`
	Note        string   `json:"note"`
	Quantity    int      `json:"quantity"`
	ToppingsStr string   `json:"topping"`
	ToppingIds  []string `json:"toppingIdList"`
	Size        string   `json:"size"`
}
