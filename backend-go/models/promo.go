package models

type Promo struct {
	ID           string   `json:"id"`
	AtLeastPrice int      `json:"minPrice"`
	MaxDiscount  int      `json:"maxPrice"`
	StoreIdList  []string `json:"stores"`
}
