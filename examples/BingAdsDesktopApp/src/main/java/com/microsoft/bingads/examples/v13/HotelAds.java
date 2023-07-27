package com.microsoft.bingads.examples.v13;

import java.rmi.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.microsoft.bingads.*;
import com.microsoft.bingads.v13.campaignmanagement.*;

// How to apply hotel group for Microsoft Hotel Campaigns.

public class HotelAds extends ExampleBase {

    private static ArrayOfAdGroupCriterionAction _hotelGroupActions = new ArrayOfAdGroupCriterionAction();
    private static long _referenceId = -1;

    public static void main(java.lang.String[] args) {

        try
        {
            authorizationData = getAuthorizationData();

            CampaignManagementExampleHelper.CampaignManagementService = new ServiceClient<ICampaignManagementService>(
                    authorizationData,
                    API_ENVIRONMENT,
                    ICampaignManagementService.class);

            // Create the hotel campaign

            ArrayOfNullableOflong campaignIds = createHotelCampaign();

            // Create the hotel ad group that will have the hotel groups.

            ArrayOfNullableOflong adGroupIds = createHotelAdGroup(campaignIds);

            // Create and update the hotel groups.

            addAndUpdateAdGroupCriterion(adGroupIds.getLongs().get(0));
            ApplyHotelGroupActionsResponse applyHotelGroupActionsResponse = addBranchAndLeafCriterion(adGroupIds.getLongs().get(0));

            long rootId = applyHotelGroupActionsResponse.getAdGroupCriterionIds().getLongs().get(1);
            long electronicsCriterionId = applyHotelGroupActionsResponse.getAdGroupCriterionIds().getLongs().get(3);
            updateBranchAndLeafCriterion(adGroupIds.getLongs().get(0), rootId, electronicsCriterionId);

            // Delete the campaign and everything it contains e.g., ad groups and ads.

            outputStatusMessage("-----\nDeleteCampaigns:");
            ArrayOflong deleteCampaignIds = new ArrayOflong();
            deleteCampaignIds.getLongs().add(campaignIds.getLongs().get(0));
            CampaignManagementExampleHelper.deleteCampaigns(
                    authorizationData.getAccountId(),
                    deleteCampaignIds);
            outputStatusMessage(String.format("Deleted CampaignId %d", deleteCampaignIds.getLongs().get(0)));
        }
        catch (Exception ex) {
            String faultXml = ExampleExceptionHelper.getBingAdsExceptionFaultXml(ex, System.out);
            outputStatusMessage(faultXml);
            String message = ExampleExceptionHelper.handleBingAdsSDKException(ex, System.out);
            outputStatusMessage(message);
        }
    }

    // create hotel campaign

    static ArrayOfNullableOflong createHotelCampaign() throws Exception {
        Campaign campaign = new Campaign();

        // set campaign type to Hotel
        ArrayList<CampaignType> campaignTypes = new ArrayList<CampaignType>();
        campaignTypes.add(CampaignType.HOTEL);
        campaign.setCampaignType(campaignTypes);

        // Hotel campaign supports commission, percent cpc and manual cpc bidding scheme
        CommissionBiddingScheme biddingScheme = new CommissionBiddingScheme();
        biddingScheme.setCommissionRate(3.14);
        campaign.setBiddingScheme(biddingScheme);

        // for hotel campaign, language should be set to "All"
        ArrayOfstring languages = new ArrayOfstring();
        languages.getStrings().add("All");
        campaign.setLanguages(languages);

        // set other fields
        campaign.setBudgetType(BudgetLimitType.DAILY_BUDGET_STANDARD);
        campaign.setDailyBudget(50.00);
        campaign.setName("Everyone's Hotel " + System.currentTimeMillis());
        campaign.setTimeZone("PacificTimeUSCanadaTijuana");
        ArrayOfCampaign campaigns = new ArrayOfCampaign();
        campaigns.getCampaigns().add(campaign);

        outputStatusMessage("-----\nAddCampaigns:");
        AddCampaignsResponse addCampaignsResponse = CampaignManagementExampleHelper.addCampaigns(
                authorizationData.getAccountId(),
                campaigns);
        ArrayOfNullableOflong campaignIds = addCampaignsResponse.getCampaignIds();
        ArrayOfBatchError campaignErrors = addCampaignsResponse.getPartialErrors();
        outputStatusMessage("CampaignIds:");
        CampaignManagementExampleHelper.outputArrayOfNullableOflong(campaignIds);
        outputStatusMessage("PartialErrors:");
        CampaignManagementExampleHelper.outputArrayOfBatchError(campaignErrors);

        return campaignIds;
    }

    // create hotel ad group

    static ArrayOfNullableOflong createHotelAdGroup(ArrayOfNullableOflong campaignIds) throws Exception {
        AdGroup adGroup = new AdGroup();

        // ad group type should be set to "HotelAds"
        adGroup.setAdGroupType("HotelAds");

        // set ad group type, should be HotelAd or PropertyAd or specify both
        ArrayList<HotelAdGroupType> hotelAdGroupTypes = new ArrayList<HotelAdGroupType>();
        hotelAdGroupTypes.add(HotelAdGroupType.HOTEL_AD);
        HotelSetting hotelSetting = new HotelSetting();
        hotelSetting.setHotelAdGroupType(hotelAdGroupTypes);
        ArrayOfSetting adGroupSettings = new ArrayOfSetting();
        adGroupSettings.getSettings().add(hotelSetting);
        adGroup.setSettings(adGroupSettings);

        // set ad group level bid value
        // Here the campaign we created is using Commission bidding scheme, so we set CommissionRate
        // if the campaign bidding scheme is PercentCpc, please set PercentCpcBid
        // if the campaign bidding scheme is ManualCpc, please set CpcBid
        RateBid rateBid = getRateBid(5.6);
        adGroup.setCommissionRate(rateBid);

        // Hotel AdGroups will inherit Hotel Campaign setting, which enforces the “All” language.
        adGroup.setLanguage(null);

        adGroup.setName("Everyone's Hotel");
        adGroup.setStartDate(null);
        Calendar calendar = Calendar.getInstance();
        adGroup.setEndDate(new com.microsoft.bingads.v13.campaignmanagement.Date());
        adGroup.getEndDate().setDay(31);
        adGroup.getEndDate().setMonth(12);
        adGroup.getEndDate().setYear(calendar.get(Calendar.YEAR));

        ArrayOfAdGroup adGroups = new ArrayOfAdGroup();
        adGroups.getAdGroups().add(adGroup);

        outputStatusMessage("-----\nAddAdGroups:");
        // when we call addAdGroups method, an ad will be automatically created for hotel ad group
        AddAdGroupsResponse addAdGroupsResponse = CampaignManagementExampleHelper.addAdGroups(
                campaignIds.getLongs().get(0),
                adGroups,
                false);
        ArrayOfNullableOflong adGroupIds = addAdGroupsResponse.getAdGroupIds();
        ArrayOfBatchError adGroupErrors = addAdGroupsResponse.getPartialErrors();
        outputStatusMessage("AdGroupIds:");
        CampaignManagementExampleHelper.outputArrayOfNullableOflong(adGroupIds);
        outputStatusMessage("PartialErrors:");
        CampaignManagementExampleHelper.outputArrayOfBatchError(adGroupErrors);

        return adGroupIds;
    }


    // Add a criterion to the ad group and then update it.

    static void addAndUpdateAdGroupCriterion(long adGroupId) throws RemoteException, Exception
    {
        // Add a biddable criterion as the root.

        HotelListing rootHotelListing = new HotelListing();
        rootHotelListing.setAttribute(null);
        rootHotelListing.setOperand("All");

        AdGroupCriterion root = addHotelGroup(
                adGroupId,
                null,
                rootHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        outputStatusMessage("-----\nApplyHotelGroupActions:");
        outputStatusMessage("Applying a biddable criterion as the root...");
        ApplyHotelGroupActionsResponse applyHotelGroupActionsResponse = CampaignManagementExampleHelper.applyHotelGroupActions(
                _hotelGroupActions);
        CampaignManagementExampleHelper.outputArrayOfNullableOflong(applyHotelGroupActionsResponse.getAdGroupCriterionIds());
        CampaignManagementExampleHelper.outputArrayOfBatchError(applyHotelGroupActionsResponse.getPartialErrors());

        ArrayList<AdGroupCriterionType> criterionType = new ArrayList<AdGroupCriterionType>();
        criterionType.add(AdGroupCriterionType.HOTEL_GROUP);

        outputStatusMessage("-----\nGetAdGroupCriterionsByIds:");
        ArrayOfAdGroupCriterion adGroupCriterions = CampaignManagementExampleHelper.getAdGroupCriterionsByIds(
                null,
                adGroupId,
                criterionType,
                null).getAdGroupCriterions();

        outputStatusMessage("Printing the ad group's hotel group; contains only the tree root node");
        printHotelGroups(adGroupCriterions);

        // Update the bid of the root node that we just added.

        BiddableAdGroupCriterion updatedRoot = new BiddableAdGroupCriterion();
        updatedRoot.setId(applyHotelGroupActionsResponse.getAdGroupCriterionIds().getLongs().get(0));
        updatedRoot.setAdGroupId(adGroupId);
        updatedRoot.setCriterionBid(getRateBid(0.40));

        _hotelGroupActions.getAdGroupCriterionActions().clear();

        addAdGroupCriterionAction(updatedRoot, ItemAction.UPDATE);

        outputStatusMessage("-----\nApplyHotelGroupActions:");
        outputStatusMessage("Updating the bid for the tree root node...");
        applyHotelGroupActionsResponse = CampaignManagementExampleHelper.applyHotelGroupActions(
                _hotelGroupActions);

        outputStatusMessage("-----\nGetAdGroupCriterionsByIds:");
        adGroupCriterions = CampaignManagementExampleHelper.getAdGroupCriterionsByIds(
                null,
                adGroupId,
                criterionType,
                null).getAdGroupCriterions();

        outputStatusMessage("Updated the bid for the tree root node");
        printHotelGroups(adGroupCriterions);
    }

    // Add branch and leaf nodes to the hotel group tree and then update it.

    static ApplyHotelGroupActionsResponse addBranchAndLeafCriterion(long adGroupId) throws RemoteException, Exception
    {
        _hotelGroupActions.getAdGroupCriterionActions().clear();

        ArrayList<AdGroupCriterionType> criterionType = new ArrayList<AdGroupCriterionType>();
        criterionType.add(AdGroupCriterionType.HOTEL_GROUP);
        outputStatusMessage("-----\nGetAdGroupCriterionsByIds:");
        ArrayOfAdGroupCriterion adGroupCriterions = CampaignManagementExampleHelper.getAdGroupCriterionsByIds(
                null,
                adGroupId,
                criterionType,
                null).getAdGroupCriterions();

        AdGroupCriterion existingRoot = getRootHotelGroupNode(adGroupCriterions);
        if (existingRoot != null)
        {
            addAdGroupCriterionAction(existingRoot, ItemAction.DELETE);
        }

        HotelListing newRootHotelListing = new HotelListing();
        newRootHotelListing.setAttribute(null);
        newRootHotelListing.setOperand("All");
        AdGroupCriterion newRoot = addHotelGroup(
                adGroupId,
                null,
                newRootHotelListing,
                HotelListingType.SUBDIVISION,
                null,
                false);

        HotelListing starRating5HotelListing = new HotelListing();
        starRating5HotelListing.setAttribute("5");
        starRating5HotelListing.setOperand("StarRating");
        AdGroupCriterion starRating5SubDivision = addHotelGroup(
                adGroupId,
                newRoot,
                starRating5HotelListing,
                HotelListingType.SUBDIVISION,
                null,
                false);

        HotelListing starRating4HotelListing = new HotelListing();
        starRating4HotelListing.setAttribute("4");
        starRating4HotelListing.setOperand("StarRating");
        AdGroupCriterion starRating4Unit = addHotelGroup(
                adGroupId,
                newRoot,
                starRating4HotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        HotelListing otherStarRatingsHotelListing = new HotelListing();
        otherStarRatingsHotelListing.setAttribute(null);
        otherStarRatingsHotelListing.setOperand("StarRating");
        AdGroupCriterion otherStarRatingsUnit = addHotelGroup(
                adGroupId,
                newRoot,
                otherStarRatingsHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        HotelListing brandAHotelListing = new HotelListing();
        brandAHotelListing.setAttribute("Brand A");
        brandAHotelListing.setOperand("Brand");
        AdGroupCriterion brandAUnit = addHotelGroup(
                adGroupId,
                starRating5SubDivision,
                brandAHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        HotelListing brandBHotelListing = new HotelListing();
        brandBHotelListing.setAttribute("Brand B");
        brandBHotelListing.setOperand("Brand");
        AdGroupCriterion brandBUnit = addHotelGroup(
                adGroupId,
                starRating5SubDivision,
                brandBHotelListing,
                HotelListingType.UNIT,
                null,
                true);

        HotelListing otherBrandsHotelListing = new HotelListing();
        otherBrandsHotelListing.setAttribute(null);
        otherBrandsHotelListing.setOperand("Brand");
        AdGroupCriterion otherBrandsUnit = addHotelGroup(
                adGroupId,
                starRating5SubDivision,
                otherBrandsHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        outputStatusMessage("-----\nApplyHotelGroupActions:");
        outputStatusMessage("Applying hotel groups to the ad group...");
        ApplyHotelGroupActionsResponse applyHotelGroupActionsResponse = CampaignManagementExampleHelper.applyHotelGroupActions(
                _hotelGroupActions);

        outputStatusMessage("-----\nGetAdGroupCriterionsByIds:");
        adGroupCriterions = CampaignManagementExampleHelper.getAdGroupCriterionsByIds(
                null,
                adGroupId,
                criterionType,
                null).getAdGroupCriterions();

        outputStatusMessage("The hotel group tree now has 7 nodes");
        printHotelGroups(adGroupCriterions);

        return applyHotelGroupActionsResponse;
    }

    // Delete and update branch and leaf criterion.

    static void updateBranchAndLeafCriterion(long adGroupId, long rootId, long starRating4CriterionId) throws RemoteException, Exception
    {
        _hotelGroupActions.getAdGroupCriterionActions().clear();

        AdGroupCriterion starRating4Criterion = new BiddableAdGroupCriterion();
        starRating4Criterion.setId(starRating4CriterionId);
        starRating4Criterion.setAdGroupId(adGroupId);
        addAdGroupCriterionAction(starRating4Criterion, ItemAction.DELETE);

        BiddableAdGroupCriterion parent = new BiddableAdGroupCriterion();
        parent.setId(rootId);

        HotelListing newStarRating4HotelListing = new HotelListing();
        newStarRating4HotelListing.setAttribute("4");
        newStarRating4HotelListing.setOperand("StarRating");

        AdGroupCriterion starRating4Subdivision = addHotelGroup(
                adGroupId,
                parent,
                newStarRating4HotelListing,
                HotelListingType.SUBDIVISION,
                null,
                false);

        HotelListing brandCHotelListing = new HotelListing();
        brandCHotelListing.setAttribute("Brand C");
        brandCHotelListing.setOperand("Brand");
        AdGroupCriterion brandCUnit = addHotelGroup(
                adGroupId,
                starRating4Subdivision,
                brandCHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        HotelListing brandDHotelListing = new HotelListing();
        brandDHotelListing.setAttribute("Brand D");
        brandDHotelListing.setOperand("Brand");
        AdGroupCriterion brandDUnit = addHotelGroup(
                adGroupId,
                starRating4Subdivision,
                brandDHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        HotelListing otherBrandsHotelListing = new HotelListing();
        otherBrandsHotelListing.setAttribute(null);
        otherBrandsHotelListing.setOperand("Brand");
        AdGroupCriterion otherBrandsUnit = addHotelGroup(
                adGroupId,
                starRating4Subdivision,
                otherBrandsHotelListing,
                HotelListingType.UNIT,
                getRateBid(0.35),
                false);

        outputStatusMessage("-----\nApplyHotelGroupActions:");
        outputStatusMessage("Updating the star rating 4 hotel group...");
        ApplyHotelGroupActionsResponse applyHotelGroupActionsResponse = CampaignManagementExampleHelper.applyHotelGroupActions(
                _hotelGroupActions);

        ArrayList<AdGroupCriterionType> criterionType = new ArrayList<AdGroupCriterionType>();
        criterionType.add(AdGroupCriterionType.HOTEL_GROUP);
        outputStatusMessage("-----\nGetAdGroupCriterionsByIds:");
        ArrayOfAdGroupCriterion adGroupCriterions = CampaignManagementExampleHelper.getAdGroupCriterionsByIds(
                null,
                adGroupId,
                criterionType,
                null).getAdGroupCriterions();

        outputStatusMessage("The hotel group tree now has 10 nodes");
        printHotelGroups(adGroupCriterions);
    }

    // Get the root criterion node.

    static AdGroupCriterion getRootHotelGroupNode(ArrayOfAdGroupCriterion adGroupCriterions)
    {
        AdGroupCriterion rootNode = null;

        for (AdGroupCriterion adGroupCriterion : adGroupCriterions.getAdGroupCriterions())
        {
            if (((HotelGroup)adGroupCriterion.getCriterion()).getParentCriterionId() == null)
            {
                rootNode = adGroupCriterion;
                break;
            }
        }

        return rootNode;
    }

    // Get a Rate bid object with the specified bid amount.

    static RateBid getRateBid(final double bidAmount)
    {
        RateAmount rateAmount = new RateAmount();
        rateAmount.setAmount(bidAmount);
        RateBid rateBid = new RateBid();
        rateBid.setRateAmount(rateAmount);

        return rateBid;
    }

    // Add a criterion action to the list of actions.

    static void addAdGroupCriterionAction(final AdGroupCriterion CRITERION, final ItemAction ITEM_ACTION)
    {
        AdGroupCriterionAction hotelGroupAction = new AdGroupCriterionAction();
        hotelGroupAction.setAction(ITEM_ACTION);
        hotelGroupAction.setAdGroupCriterion(CRITERION);

        _hotelGroupActions.getAdGroupCriterionActions().add(hotelGroupAction);
    }

    // Add either a negative or biddable hotel group criterion.

    static AdGroupCriterion addHotelGroup(
            long adGroupId,
            AdGroupCriterion parent,
            HotelListing hotelListing,
            HotelListingType hotelListingType,
            RateBid bid,
            Boolean isNegative)
    {
        AdGroupCriterion adGroupCriterion = null;

        if (isNegative)
        {
            adGroupCriterion = new NegativeAdGroupCriterion();
        }
        else
        {
            adGroupCriterion = new BiddableAdGroupCriterion();
            ((BiddableAdGroupCriterion)adGroupCriterion).setCriterionBid(bid);
        }

        adGroupCriterion.setAdGroupId(adGroupId);

        HotelGroup hotelGroup = new HotelGroup();
        hotelGroup.setListing(hotelListing);
        hotelGroup.setParentCriterionId((parent != null) ? parent.getId() : null);

        if (hotelListingType == HotelListingType.SUBDIVISION)
        {
            hotelGroup.setListingType(HotelListingType.SUBDIVISION);  // Branch
            adGroupCriterion.setId(_referenceId--);
        }
        else
        {
            hotelGroup.setListingType(HotelListingType.UNIT);;  // Leaf
        }

        adGroupCriterion.setCriterion(hotelGroup);

        addAdGroupCriterionAction(adGroupCriterion, ItemAction.ADD);

        return adGroupCriterion;
    }

    // Print the hotel group

    static void printHotelGroups(ArrayOfAdGroupCriterion adGroupCriterions)
    {
        Map<Long, ArrayList<AdGroupCriterion>> childBranches = new HashMap<Long, ArrayList<AdGroupCriterion>>();
        AdGroupCriterion treeRoot = null;

        for (AdGroupCriterion adGroupCriterion : adGroupCriterions.getAdGroupCriterions())
        {
            HotelGroup hotelGroup = (HotelGroup)adGroupCriterion.getCriterion();
            childBranches.put(adGroupCriterion.getId(), new ArrayList<AdGroupCriterion>());

            if (hotelGroup.getParentCriterionId() != null)
            {
                childBranches.get(hotelGroup.getParentCriterionId()).add(adGroupCriterion);
            }
            else
            {
                treeRoot = adGroupCriterion;
            }
        }

        printHotelGroupTree(treeRoot, childBranches, 0);
    }

    // Print the hotel group tree.

    static void printHotelGroupTree(
            AdGroupCriterion node,
            Map<Long, ArrayList<AdGroupCriterion>> childBranches,
            int treeLevel)
    {
        HotelGroup criterion = (HotelGroup) node.getCriterion();

        outputStatusMessage(String.format("%" + ((treeLevel > 0) ? treeLevel * 4 : "") + "s%s\n",
                "",
                criterion.getListingType()));

        outputStatusMessage(String.format("%" + ((treeLevel > 0) ? treeLevel * 4 : "") + "s%s%d\n",
                "",
                "ParentCriterionId: ",
                criterion.getParentCriterionId()));

        outputStatusMessage(String.format("%" + ((treeLevel > 0) ? treeLevel * 4 : "") + "s%s%d\n",
                "",
                "Id: ",
                node.getId()));

        if (criterion.getListingType() == HotelListingType.UNIT)
        {
            if (node instanceof BiddableAdGroupCriterion)
            {
                outputStatusMessage(String.format("%" + ((treeLevel > 0) ? treeLevel * 4 : "") + "s%s%.2f\n",
                        "",
                        "Bid amount: ",
                        ((RateBid)((BiddableAdGroupCriterion)node).getCriterionBid()).getRateAmount().getAmount()));

            }
            else
            {
                if (node instanceof NegativeAdGroupCriterion)
                {
                    outputStatusMessage(String.format("%" + treeLevel * 4 + "s%s\n",
                            "",
                            "Not bidding on this listing"));
                }
            }
        }

        String nullAttribute = (criterion.getParentCriterionId() != null) ? "(All Others)" : "(Tree Root)";

        outputStatusMessage(String.format("%" + ((treeLevel > 0) ? treeLevel * 4 : "") + "s%s%s\n",
                "",
                "Attribute: ",
                (criterion.getListing().getAttribute() == null) ? nullAttribute : criterion.getListing().getAttribute()));

        outputStatusMessage(String.format("%" + ((treeLevel > 0) ? treeLevel * 4 : "") + "s%s%s\n",
                "",
                "Listing: ",
                criterion.getListing().getOperand()));

        for (AdGroupCriterion childNode : childBranches.get(node.getId()))
        {
            printHotelGroupTree(childNode, childBranches, treeLevel + 1);
        }
    }
}

