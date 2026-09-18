package campus.lostfound.model;

import campus.lostfound.enums.ClaimStatus;

public class Claim {
    private int claimId;
    private int lostItemId;
    private int foundItemId;
    private int claimantUserId;
    private String proofDetails;
    private ClaimStatus status;
    private String claimDate;

    // Helper display fields
    private String foundItemTitle;
    private String claimantName;
    private String claimantContact;

    public Claim() {
        this.status = ClaimStatus.PENDING;
    }

    public Claim(int claimId, int lostItemId, int foundItemId, int claimantUserId,
                 String proofDetails, ClaimStatus status, String claimDate) {
        this.claimId = claimId;
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.claimantUserId = claimantUserId;
        this.proofDetails = proofDetails;
        this.status = (status != null) ? status : ClaimStatus.PENDING;
        this.claimDate = claimDate;
    }

    public void displaySummary() {
        System.out.printf("Claim #%d | Found Item #%d (%s) | By User #%d | Status: %s | Date: %s%n",
                claimId, foundItemId, (foundItemTitle != null ? foundItemTitle : "N/A"),
                claimantUserId, status, (claimDate != null ? claimDate : "N/A"));
        System.out.println("   Proof Details: " + proofDetails);
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public int getLostItemId() {
        return lostItemId;
    }

    public void setLostItemId(int lostItemId) {
        this.lostItemId = lostItemId;
    }

    public int getFoundItemId() {
        return foundItemId;
    }

    public void setFoundItemId(int foundItemId) {
        this.foundItemId = foundItemId;
    }

    public int getClaimantUserId() {
        return claimantUserId;
    }

    public void setClaimantUserId(int claimantUserId) {
        this.claimantUserId = claimantUserId;
    }

    public String getProofDetails() {
        return proofDetails;
    }

    public void setProofDetails(String proofDetails) {
        this.proofDetails = proofDetails;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(String claimDate) {
        this.claimDate = claimDate;
    }

    public String getFoundItemTitle() {
        return foundItemTitle;
    }

    public void setFoundItemTitle(String foundItemTitle) {
        this.foundItemTitle = foundItemTitle;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantContact() {
        return claimantContact;
    }

    public void setClaimantContact(String claimantContact) {
        this.claimantContact = claimantContact;
    }
}
