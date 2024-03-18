package de.cidaas.quarkus.extension;

public class AddressValidationResult {
	private String street;
	private String houseNumber;
	private String zipCode;
	private String city;
	private String district;
	private String country;
	private String points;
	private String resultcode;
	private String resulttext;
	private String trafficlight;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getResultcode() {
		return resultcode;
	}

	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}

	public String getResulttext() {
		return resulttext;
	}

	public void setResulttext(String resulttext) {
		this.resulttext = resulttext;
	}

	public String getTrafficlight() {
		return trafficlight;
	}

	public void setTrafficlight(String trafficlight) {
		this.trafficlight = trafficlight;
	}

}
