class GeneralView {}

let beacon;

GeneralView.Views = class {
    static get TOGGLE_CONNECTION_ID() { return "tglConnection"; }
    static get LABEL_MANUFACTURER_ID() { return  "lblManufacturer"; }
    static get LABEL_MODEL_ID() { return  "lblModel"; }
    static get LABEL_SOFTWARE_VERSION_ID() { return  "lblSoftwareVersion"; }
    static get LABEL_HARDWARE_VERSION_ID() { return  "lblHardwareVersion"; }
    static get LABEL_FIRMWARE_VERSION_ID() { return  "lblFirmwareVersion"; }
    static get LABEL_OS_VERSION_ID() { return  "lblOsVersion"; }
    static get LABEL_MAC_ADDRESS_ID() { return  "lblMacAddress"; }
    static get LABEL_SUPPORTED_TX_POWER_ID() { return  "lblSupportedTxPower"; }
    static get LABEL_SUPPORTED_SLOTS_ID() { return  "lblSupportedSlots"; }
    static get LABEL_ADV_FEATURED_ID() { return  "lblAdvFeature"; }
    static get LABEL_SLOT_AMOUNT_ID() { return  "lblSlotAmount"; }
 }

 GeneralView.ViewsAction = class{
    static setConnection(value){
        $(`#${GeneralView.Views.TOGGLE_CONNECTION_ID}`).prop("checked",value);
        // not implemented yet
        // native.onDataChanged(JSON.stringify(beacon));
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

    static setLabelOsVersion(value) {
            $(`#${GeneralView.Views.LABEL_OS_VERSION_ID}`).html(value);
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

    static getLabelOsVersion() {
        return $(`#${GeneralView.Views.LABEL_OS_VERSION_ID}`).html();
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

    static addLinesAfterCommas(text) {
        let parts = text.split(",");
        return parts.join(", ");
    }

    static setBeaconInformation(beaconString){
        beacon = JSON.parse(beaconString);

        let generalParams = beacon.parameters.parameters["Basic config"];
        let paramMap = {};
        for(var i in generalParams) {
            paramMap[generalParams[i].name] = generalParams[i].value;
        }

        let supportedTxPowers = this.addLinesAfterCommas(paramMap["Supported TX powers"]); //this is needed in order to enable automatic word-wrap if there is not enough space for whole text to fit in one line

        GeneralView.ViewsAction.setConnection(true);
        GeneralView.ViewsAction.setLabelMacAddress(paramMap["MAC"]);
        GeneralView.ViewsAction.setLabelManufacturer(paramMap["Manufacturer"]);
        GeneralView.ViewsAction.setLabelModel(paramMap["Model"]);
        GeneralView.ViewsAction.setLabelSoftwareVersion(paramMap["Softdevice version"]);
        GeneralView.ViewsAction.setLabelHardwareVersion(paramMap["Hardware version"]);
        GeneralView.ViewsAction.setLabelFirmwareVersion(paramMap["Firmware version"]);
        GeneralView.ViewsAction.setLabelOsVersion(paramMap["FreeRTOS version"]);
        GeneralView.ViewsAction.setLabelSupportedTxPower(supportedTxPowers);
        GeneralView.ViewsAction.setLabelSupportedSlots("EMPTY, CUSTOM, URL, I_BEACON, DEFAULT");
        GeneralView.ViewsAction.setLabelSlotAmount(beacon.slots.slots.length);
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


