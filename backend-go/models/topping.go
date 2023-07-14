package models

type Topping struct {
	ImageUrl string `json:"image"`
	Name     string `json:"name"`
	Price    int    `json:"price"`
}
