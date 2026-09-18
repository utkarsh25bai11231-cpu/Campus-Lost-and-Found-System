package campus.lostfound.service;

import campus.lostfound.enums.ClaimStatus;
import campus.lostfound.enums.ItemStatus;
import campus.lostfound.exception.ClaimException;
import campus.lostfound.exception.InvalidItemException;
import campus.lostfound.model.Claim;
import campus.lostfound.model.Item;
import campus.lostfound.model.Staff;
import campus.lostfound.model.User;
import campus.lostfound.repository.ClaimRepository;
import campus.lostfound.repository.ItemRepository;
import campus.lostfound.repository.UserRepository;

import java.util.List;

public class ClaimService {
    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ClaimService() {
        this.claimRepository = new ClaimRepository();
        this.itemRepository = new ItemRepository();
        this.userRepository = new UserRepository();
    }

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = new UserRepository();
    }

    public Claim submitClaim(int lostItemId, int foundItemId, int claimantUserId, String proofDetails)
            throws ClaimException, InvalidItemException {
        if (proofDetails == null || proofDetails.trim().isEmpty()) {
            throw new ClaimException("Proof of ownership details are mandatory to claim an item.");
        }

        Item foundItem = itemRepository.findById(foundItemId);
        if (foundItem == null) {
            throw new InvalidItemException("Found item not found with ID: " + foundItemId);
        }
        if (!"FOUND".equalsIgnoreCase(foundItem.getReportType())) {
            throw new ClaimException("Claims can only be filed against FOUND items.");
        }
        if (foundItem.getStatus() == ItemStatus.CLAIMED) {
            throw new ClaimException("This item has already been successfully claimed and closed.");
        }
        if (foundItem.getStatus() == ItemStatus.CLOSED) {
            throw new ClaimException("This report has been closed and cannot be claimed.");
        }
        if (foundItem.getUserId() == claimantUserId) {
            throw new ClaimException("Invalid claim: You cannot claim an item that you reported finding.");
        }

        // Prevent duplicate claims by the same user on the same found item
        if (claimRepository.hasActiveClaim(foundItemId, claimantUserId)) {
            throw new ClaimException("You already have an active (pending or approved) claim on this item.");
        }

        Claim claim = new Claim(0, lostItemId, foundItemId, claimantUserId, proofDetails.trim(), ClaimStatus.PENDING, null);
        Claim saved = claimRepository.save(claim);

        // Mark item as CLAIM_PENDING
        itemRepository.updateStatus(foundItemId, ItemStatus.CLAIM_PENDING);

        return saved;
    }

    public void approveClaim(int claimId, int reviewerUserId) throws ClaimException, InvalidItemException {
        Claim claim = claimRepository.findById(claimId);
        if (claim == null) {
            throw new ClaimException("Claim not found with ID: " + claimId);
        }
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ClaimException("Only PENDING claims can be approved.");
        }

        Item foundItem = itemRepository.findById(claim.getFoundItemId());
        if (foundItem == null) {
            throw new InvalidItemException("Associated found item not found.");
        }

        // Only the user who found the item or a staff member can approve claims.
        User reviewer = userRepository.findById(reviewerUserId);
        if (reviewer == null || (foundItem.getUserId() != reviewerUserId && !(reviewer instanceof Staff))) {
            throw new ClaimException("Permission denied: only the finder or a staff member can approve claims.");
        }

        claimRepository.updateStatus(claimId, ClaimStatus.APPROVED);
        itemRepository.updateStatus(claim.getFoundItemId(), ItemStatus.CLAIMED);

        if (claim.getLostItemId() > 0) {
            itemRepository.updateStatus(claim.getLostItemId(), ItemStatus.CLAIMED);
        }

        // Automatically reject all other pending claims on this now claimed item
        List<Claim> allItemClaims = claimRepository.findByFoundItemId(claim.getFoundItemId());
        for (Claim other : allItemClaims) {
            if (other.getClaimId() != claimId && other.getStatus() == ClaimStatus.PENDING) {
                claimRepository.updateStatus(other.getClaimId(), ClaimStatus.REJECTED);
            }
        }
    }

    public void rejectClaim(int claimId) throws ClaimException {
        Claim claim = claimRepository.findById(claimId);
        if (claim == null) {
            throw new ClaimException("Claim not found with ID: " + claimId);
        }
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ClaimException("Only PENDING claims can be rejected.");
        }

        claimRepository.updateStatus(claimId, ClaimStatus.REJECTED);

        // Check if other pending claims still exist for this found item
        List<Claim> itemClaims = claimRepository.findByFoundItemId(claim.getFoundItemId());
        boolean hasOtherPending = false;
        for (Claim c : itemClaims) {
            if (c.getClaimId() != claimId && c.getStatus() == ClaimStatus.PENDING) {
                hasOtherPending = true;
                break;
            }
        }
        if (!hasOtherPending) {
            itemRepository.updateStatus(claim.getFoundItemId(), ItemStatus.OPEN);
        }
    }

    public List<Claim> getClaimsByClaimant(int userId) {
        return claimRepository.findByClaimantUserId(userId);
    }

    public List<Claim> getClaimsForFoundItem(int foundItemId) {
        return claimRepository.findByFoundItemId(foundItemId);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
}
