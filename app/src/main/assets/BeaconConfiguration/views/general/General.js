class GeneralView {}

GeneralView.Views = class {
    static get TOGGLE_CONNECTION_ID() { return "tglConnection"; }
    static get LABLE_MANUFACTURER_ID() { return  "lblManufacturer"; }
    static get LABLE_MODEL_ID() { return  "lblModel"; }
    static get LABLE_SOFTWARE_VERSION_ID() { return  "lblSoftwareVersion"; }
    static get LABLE_HARDWARE_VERSION_ID() { return  "lblHardwareVersion"; }
    static get LABLE_FIRMWARE_VERSION_ID() { return  "lblFirmwareVersion"; }
    static get LABLE_MAC_ADDRESS_ID() { return  "lblMacAddress"; }
    static get LABLE_SUPPORTED_TX_POWER_ID() { return  "lblSupportedTxPower"; } 
    static get LABLE_SUPPORTED_SLOTS_ID() { return  "lblSupportedSlots"; }
    static get LABLE_ADV_FEATURED_ID() { return  "lblAdvFeature"; }
    static get LABLE_SLOT_AMOUNT_ID() { return  "lblSlotAmount"; }
 }

 GeneralView.ViewsAction = class{
    static setConnection(value){
        $(`#${GeneralView.Views.TOGGLE_CONNECTION_ID}`).val(value);
    }
    
    static setLableManufacturer(value) {
        $(`#${GeneralView.Views.LABLE_MANUFACTURER_ID}`).html(value);
    }
    
    static setLableModel(value) {
        $(`#${GeneralView.Views.LABLE_MODEL_ID}`).html(value);
    }
    
    static setLableSoftwareVersion(value) {
        $(`#${GeneralView.Views.LABLE_SOFTWARE_VERSION_ID}`).html(value);
    }
    
    static setLableHardwareVersion(value) {
        $(`#${GeneralView.Views.LABLE_HARDWARE_VERSION_ID}`).html(value);
    }
    
    static setLableFirmwareVersion(value) {
        $(`#${GeneralView.Views.LABLE_FIRMWARE_VERSION_ID}`).html(value);
    }
    
    static setLableMacAddress(value) {
        $(`#${GeneralView.Views.LABLE_MAC_ADDRESS_ID}`).html(value);
    }
    
    static setLableSupportedTxPower(value) {
        $(`#${GeneralView.Views.LABLE_SUPPORTED_TX_POWER_ID}`).html(value);
    }
    
    static setLableSupportedSlots(value) {
        $(`#${GeneralView.Views.LABLE_SUPPORTED_SLOTS_ID}`).html(value);
    }
    
    static setLableAdvFeature(value) {
        $(`#${GeneralView.Views.LABLE_ADV_FEATURED_ID}`).html(value);
    }
    
    static setLableSlotAmount(value) {
        $(`#${GeneralView.Views.LABLE_SLOT_AMOUNT_ID}`).html(value);
    }
    
    static getConnection() {
        return $(`#${GeneralView.Views.TOGGLE_CONNECTION_ID}`).html();
    }
    
    static getLableManufacturer() {
        return $(`#${GeneralView.Views.LABLE_MANUFACTURER_ID}`).html();
    }
    
    static getLableModel() {
        return $(`#${GeneralView.Views.LABLE_MODEL_ID}`).html();
    }
    
    static getLableSoftwareVersion() {
        return $(`#${GeneralView.Views.LABLE_SOFTWARE_VERSION_ID}`).html();
    }
    
    static getLableHardwareVersion() {
        return $(`#${GeneralView.Views.LABLE_HARDWARE_VERSION_ID}`).html();
    }
    
    static getLableFirmwareVersion() {
        return $(`#${GeneralView.Views.LABLE_FIRMWARE_VERSION_ID}`).html();
    }
    
    static getLableMacAddress() {
        return $(`#${GeneralView.Views.LABLE_MAC_ADDRESS_ID}`).html();
    }
    
    static getLableSupportedTxPower() {
        return $(`#${GeneralView.Views.LABLE_SUPPORTED_TX_POWER_ID}`).html();
    }
    
    static getLableSupportedSlots() {
        return $(`#${GeneralView.Views.LABLE_SUPPORTED_SLOTS_ID}`).html();
    }
    
    static getLableAdvFeature() {
        return $(`#${GeneralView.Views.LABLE_ADV_FEATURED_ID}`).html();
    }
    
    static getLableSlotAmount() {
        return $(`#${GeneralView.Views.LABLE_SLOT_AMOUNT_ID}`).html();
    }
}

GeneralView.Actions = class{
    static setBeaconInformation(beacon){
        GeneralView.ViewsAction.setConnection(beacon.connectable);
        GeneralView.ViewsAction.setLableMacAddress(beacon.address);
        GeneralView.ViewsAction.setLableManufacturer(beacon.manufacturer);
        GeneralView.ViewsAction.setLableModel(beacon.model);
        GeneralView.ViewsAction.setLableSoftwareVersion(beacon.softwareVersion);
        GeneralView.ViewsAction.setLableHardwareVersion(beacon.hardwareVersion);
        GeneralView.ViewsAction.setLableFirmwareVersion(beacon.firmwareVersion);
        GeneralView.ViewsAction.setLableSupportedTxPower(beacon.supportedTxPower);
        GeneralView.ViewsAction.setLableSupportedSlots(beacon.supportedSlots);
        GeneralView.ViewsAction.setLableAdvFeature(beacon.advFeature);
        GeneralView.ViewsAction.setLableSlotAmount(beacon.slotAmmount);
    }
}


