class GeneralView {}

GeneralView.Views = class {
    static get TOGGLE_CONNECTION_ID() { return "tglConnection"; }
    static get LABEL_MANUFACTURER_ID() { return  "lblManufacturer"; }
    static get LABEL_MODEL_ID() { return  "lblModel"; }
    static get LABEL_SOFTWARE_VERSION_ID() { return  "lblSoftwareVersion"; }
    static get LABEL_HARDWARE_VERSION_ID() { return  "lblHardwareVersion"; }
    static get LABEL_FIRMWARE_VERSION_ID() { return  "lblFirmwareVersion"; }
    static get LABEL_MAC_ADDRESS_ID() { return  "lblMacAddress"; }
    static get LABEL_SUPPORTED_TX_POWER_ID() { return  "lblSupportedTxPower"; }
    static get LABEL_SUPPORTED_SLOTS_ID() { return  "lblSupportedSlots"; }
    static get LABEL_ADV_FEATURED_ID() { return  "lblAdvFeature"; }
    static get LABEL_SLOT_AMOUNT_ID() { return  "lblSlotAmount"; }
 }

 GeneralView.ViewsAction = class{
    static setConnection(value){
        $(`#${GeneralView.Views.TOGGLE_CONNECTION_ID}`).prop("checked",value);
        native.changeConnectible(value);
    }
    
    static setLabelManufacturer(value) {
        $(`#${GeneralView.Views.LABEL_MANUFACTURER_ID}`).html(value);
    }
    
    static setLabelModel(value) {
        $(`#${GeneralView.Views.LABEL_MODEL_ID}`).html(value);
    }
    
    static setLabelSoftwareVersion(value) {
        $(`#${GeneralView.Views.LABEL_SOFTWARE_VERSION_ID}`).html(value);
    }
    
    static setLabelHardwareVersion(value) {
        $(`#${GeneralView.Views.LABEL_HARDWARE_VERSION_ID}`).html(value);
    }
    
    static setLabelFirmwareVersion(value) {
        $(`#${GeneralView.Views.LABEL_FIRMWARE_VERSION_ID}`).html(value);
    }
    
    static setLabelMacAddress(value) {
        $(`#${GeneralView.Views.LABEL_MAC_ADDRESS_ID}`).html(value);
    }
    
    static setLabelSupportedTxPower(value) {
        $(`#${GeneralView.Views.LABEL_SUPPORTED_TX_POWER_ID}`).html(value);
    }
    
    static setLabelSupportedSlots(value) {
        $(`#${GeneralView.Views.LABEL_SUPPORTED_SLOTS_ID}`).html(value);
    }
    
    static setLabelAdvFeature(value) {
        $(`#${GeneralView.Views.LABEL_ADV_FEATURED_ID}`).html(value);
    }
    
    static setLabelSlotAmount(value) {
        $(`#${GeneralView.Views.LABEL_SLOT_AMOUNT_ID}`).html(value);
    }
    
    static getConnection() {
        return $(`#${GeneralView.Views.TOGGLE_CONNECTION_ID}`).html();
    }
    
    static getLabelManufacturer() {
        return $(`#${GeneralView.Views.LABEL_MANUFACTURER_ID}`).html();
    }
    
    static getLabelModel() {
        return $(`#${GeneralView.Views.LABEL_MODEL_ID}`).html();
    }
    
    static getLabelSoftwareVersion() {
        return $(`#${GeneralView.Views.LABEL_SOFTWARE_VERSION_ID}`).html();
    }
    
    static getLabelHardwareVersion() {
        return $(`#${GeneralView.Views.LABEL_HARDWARE_VERSION_ID}`).html();
    }
    
    static getLabelFirmwareVersion() {
        return $(`#${GeneralView.Views.LABEL_FIRMWARE_VERSION_ID}`).html();
    }
    
    static getLabelMacAddress() {
        return $(`#${GeneralView.Views.LABEL_MAC_ADDRESS_ID}`).html();
    }
    
    static getLabelSupportedTxPower() {
        return $(`#${GeneralView.Views.LABEL_SUPPORTED_TX_POWER_ID}`).html();
    }
    
    static getLabelSupportedSlots() {
        return $(`#${GeneralView.Views.LABEL_SUPPORTED_SLOTS_ID}`).html();
    }
    
    static getLabelAdvFeature() {
        return $(`#${GeneralView.Views.LABEL_ADV_FEATURED_ID}`).html();
    }
    
    static getLabelSlotAmount() {
        return $(`#${GeneralView.Views.LABEL_SLOT_AMOUNT_ID}`).html();
    }
}

GeneralView.Actions = class{
    static setBeaconInformation(beacon){
        GeneralView.ViewsAction.setConnection(beacon.connectible);
        GeneralView.ViewsAction.setLabelMacAddress(beacon.address);
        GeneralView.ViewsAction.setLabelManufacturer(beacon.manufacturer);
        GeneralView.ViewsAction.setLabelModel(beacon.model);
        GeneralView.ViewsAction.setLabelSoftwareVersion(beacon.softwareVersion);
        GeneralView.ViewsAction.setLabelHardwareVersion(beacon.hardwareVersion);
        GeneralView.ViewsAction.setLabelFirmwareVersion(beacon.firmwareVersion);
        GeneralView.ViewsAction.setLabelSupportedTxPower(beacon.supportedTxPower);
        GeneralView.ViewsAction.setLabelSupportedSlots(beacon.supportedSlots);
        GeneralView.ViewsAction.setLabelAdvFeature(beacon.advFeature);
        GeneralView.ViewsAction.setLabelSlotAmount(beacon.slotAmount);
    }

    static updateFirmware(){
        native.updateFirmware();
    }

    static factoryReset(){
        native.factoryReset();
    }

    static addPassword(){
        native.addPassword();
    }

    static powerOff(){
        native.powerOff();
    }

    static changeConnectible(value){
        native.changeConnectible(value);
    }

}


