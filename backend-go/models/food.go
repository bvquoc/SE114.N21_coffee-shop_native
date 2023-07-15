package models

type Food struct {
	Name   string   `json:"name"`
	Price  int      `json:"price"`
	Images []string `json:"images"`
}
