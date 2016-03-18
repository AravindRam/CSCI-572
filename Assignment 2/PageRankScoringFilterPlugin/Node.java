package team1.PageRankScoringFilterPlugin;

public class Node {
	
	private String id;
	private String siteName;
	private String images;
	private String keywords;
	private String title;	
	private String content;
	private double price;
	private String category;	
	
	//TODO : Util to get actual address and vice versa using Google API call
	//URL https://github.com/googlemaps/google-maps-services-java/
	private double Geographical_Latitude;	//Co-ordinates of exact location (Latitude, Longitude)
	private double Geographical_Longitude;
	private String Geographical_Name_State;	//State Name
	private String Geographical_Name;		//City Name
	private String availableFrom;			//Location where arms available
	
	//Seller attributes
	private String sellerContentDescription;	
	private String sellerContactName;
	private String sellerTelephoneNumber;
	private String sellerUrl;
	private String sellerStartDate;
	private String sellerType;
	
	//Buyer attributes
	private String buyerDescription;
	private String buyerStartDate;
	private String buyerType;	
	private String buyerOrgName;
	
	private double page_rank;
	private String version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public double getGeographical_Latitude() {
		return Geographical_Latitude;
	}
	public void setGeographical_Latitude(double geographical_Latitude) {
		Geographical_Latitude = geographical_Latitude;
	}
	public double getGeographical_Longitude() {
		return Geographical_Longitude;
	}
	public void setGeographical_Longitude(double geographical_Longitude) {
		Geographical_Longitude = geographical_Longitude;
	}
	public String getGeographical_Name_State() {
		return Geographical_Name_State;
	}
	public void setGeographical_Name_State(String geographical_Name_State) {
		Geographical_Name_State = geographical_Name_State;
	}
	public String getGeographical_Name() {
		return Geographical_Name;
	}
	public void setGeographical_Name(String geographical_Name) {
		Geographical_Name = geographical_Name;
	}
	public String getAvailableFrom() {
		return availableFrom;
	}
	public void setAvailableFrom(String availableFrom) {
		this.availableFrom = availableFrom;
	}
	public String getSellerContentDescription() {
		return sellerContentDescription;
	}
	public void setSellerContentDescription(String sellerContentDescription) {
		this.sellerContentDescription = sellerContentDescription;
	}
	public String getSellerContactName() {
		return sellerContactName;
	}
	public void setSellerContactName(String sellerContactName) {
		this.sellerContactName = sellerContactName;
	}
	public String getSellerTelephoneNumber() {
		return sellerTelephoneNumber;
	}
	public void setSellerTelephoneNumber(String sellerTelephoneNumber) {
		this.sellerTelephoneNumber = sellerTelephoneNumber;
	}
	public String getSellerUrl() {
		return sellerUrl;
	}
	public void setSellerUrl(String sellerUrl) {
		this.sellerUrl = sellerUrl;
	}
	public String getSellerStartDate() {
		return sellerStartDate;
	}
	public void setSellerStartDate(String sellerStartDate) {
		this.sellerStartDate = sellerStartDate;
	}
	public String getSellerType() {
		return sellerType;
	}
	public void setSellerType(String sellerType) {
		this.sellerType = sellerType;
	}
	public String getBuyerDescription() {
		return buyerDescription;
	}
	public void setBuyerDescription(String buyerDescription) {
		this.buyerDescription = buyerDescription;
	}
	public String getBuyerStartDate() {
		return buyerStartDate;
	}
	public void setBuyerStartDate(String buyerStartDate) {
		this.buyerStartDate = buyerStartDate;
	}
	public String getBuyerType() {
		return buyerType;
	}
	public void setBuyerType(String buyerType) {
		this.buyerType = buyerType;
	}
	public String getBuyerOrgName() {
		return buyerOrgName;
	}
	public void setBuyerOrgName(String buyerOrgName) {
		this.buyerOrgName = buyerOrgName;
	}
	public double getPage_rank() {
		return page_rank;
	}
	public void setPage_rank(double page_rank) {
		this.page_rank = page_rank;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "Node [id=" + id + "]";
	}	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}		
	
}
	
	
	
	