class Beacon {

    constructor(){
        this._name = "";
        this._address = "";
        this._connectable = true;
        this._rssi = 0;
        this._manufacturer = "Aconno";
        this._model = "V1";
        this._softwareVersion = "1";
        this._hardwareVersion = "1";
        this._firmwareVersion = "1";
        this._advFeature = "N/a";
        this._supportedTxPower = new Int32Array();
        this._supportedSlots = new Array();
        this._slotAmmount = 0;
        this._slots = new Array();
        this._parameters = {}; 
        this._abstractData = "";
        this._abstractDataMapped = {};
    }

    get name() {
        return this._name;
    }
    set name(value) {
        this._name = value;
    }
    get address() {
        return this._address;
    }
    set address(value) {
        this._address = value;
    }
    get connectable() {
        return this._connectable;
    }
    set connectable(value) {
        this._connectable = value;
    }
    get rssi() {
        return this._rssi;
    }
    set rssi(value) {
        this._rssi = value;
    }
    get manufacturer() {
        return this._manufacturer;
    }
    set manufacturer(value) {
        this._manufacturer = value;
    }
    get model() {
        return this._model;
    }
    set model(value) {
        this._model = value;
    }
    get softwareVersion() {
        return this._softwareVersion;
    }
    set softwareVersion(value) {
        this._softwareVersion = value;
    }
    get hardwareVersion() {
        return this._hardwareVersion;
    }
    set hardwareVersion(value) {
        this._hardwareVersion = value;
    }
    get firmwareVersion() {
        return this._firmwareVersion;
    }
    set firmwareVersion(value) {
        this._firmwareVersion = value;
    }
    get advFeature() {
        return this._advFeature;
    }
    set advFeature(value) {
        this._advFeature = value;
    }
    get supportedTxPower() {
        return this._supportedTxPower;
    }
    set supportedTxPower(value) {
        this._supportedTxPower = value;
    }
    get supportedSlots() {
        return this._supportedSlots;
    }
    set supportedSlots(value) {
        this._supportedSlots = value;
    }
    get slotAmmount() {
        return this._slotAmmount;
    }
    set slotAmmount(value) {
        this._slotAmmount = value;
    }
    get slots() {
        return this._slots;
    }
    set slots(value) {
        this._slots = value;
    }
    get parameters() {
        return this._parameters;
    }
    set parameters(value) {
        this._parameters = value;
    }
    get abstractData() {
        return this._abstractData;
    }
    set abstractData(value) {
        this._abstractData = value;
    }
    get abstractDataMapped() {
        return this._abstractDataMapped;
    }
    set abstractDataMapped(value) {
        this._abstractDataMapped = value;
    }

    static getTestBeacon() {
        let beacon = new Beacon();
        beacon.connectable = true;
        beacon.address = "33:3";
        beacon.manufacturer = "asd";
        beacon.model = "model";
        beacon.softwareVersion = "soft";
        beacon.hardwareVersion = "whatves";
        beacon.firmwareVersion = "firmware";
        beacon.supportedTxPower = "whatever";
        beacon.supportedSlots = "1";
        return beacon;
    }
}