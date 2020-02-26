
class Fragment {
    constructor(pageUrl,onInformationLoaded,onInformationUpdate) {
        this.pageUrl = pageUrl;
        this.onInformationLoaded = onInformationLoaded;
        this.onInformationUpdate = onInformationUpdate;
    }
}


var fragments = [
    new Fragment("../general/General.html",function(beaconInfo) {
        GeneralView.Actions.setBeaconInformation(beaconInfo);
    },null),
    new Fragment("../arbitrary/ArbitraryData.html",function(beaconInfo) {
        onArbitraryDataDocumentLoaded();
        initArbitraryData(beaconInfo);
    },
    function(beaconInfo) {
            initArbitraryData(beaconInfo);
        }
    ),
    new Fragment("../parameters/Parameters.html",function(beaconInfo) {
        ParametersLoader.setBeaconParameters(beaconInfo);
    },null)
]


var viewPager;
$(document).ready(function() {
    initPager();
})

function initPager() {
    zuix.context('settings-view-pager', function(){
        viewPager = this;

        console.log("VIEW PAGER INITIALIZED");

        viewPager.on('page:change', function(e, pageInfo) {
            // new page number
            console.log(pageInfo.in);
            // old page number
            console.log(pageInfo.out);
            //TODO select active tab
        })

    });
}

var informationAlreadyLoaded = false;
function setBeaconInformation(beaconInfo) {
    let beacon = JSON.parse(beaconInfo);
    if(informationAlreadyLoaded) {
        for (i = 0; i < fragments.length; i++) {
            if(fragments[i].onInformationUpdate != null) {
                console.log("UPDATING FRAGMENT "+i);
                fragments[i].onInformationUpdate(beaconInfo);
            }
        }
        return;
    }

    loadViewPagerPagesWithBeaconInfo(beaconInfo)
    informationAlreadyLoaded = true;
}



function loadViewPagerPagesWithBeaconInfo(beaconInfo) {

    var pagerDiv = document.getElementById("settings-view-pager-id");

    for (i = 0; i < fragments.length; i++) {
        var pagerPage = document.createElement("div");
        let pageContentContainer = document.createElement("div");
        pageContentContainer.classList.add("fullscreen");
        pageContentContainer.classList.add("margin-sides");
        pagerPage.appendChild(pageContentContainer);
        pagerDiv.appendChild(pagerPage);

        let fragment = fragments[i];

        $.get(fragment.pageUrl, function(data, status){
            pageContentContainer.innerHTML = data;
            fragment.onInformationLoaded(beaconInfo);
        });

    }
}




