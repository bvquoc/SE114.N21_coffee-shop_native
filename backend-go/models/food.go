package models

type Food struct {
	ID       string   `json:"id"`
	Note     string   `json:"note"`
	Quantity int      `json:"quantity"`
	Toppings []string `json:"topping"`
	Size     string   `json:"size"`
}
