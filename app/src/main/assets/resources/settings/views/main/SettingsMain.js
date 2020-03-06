
class Fragment {
    constructor(name,pageUrl,onInformationLoaded,onInformationUpdate,preprocessPage) {
        this.name = name;
        this.pageUrl = pageUrl;
        this.onInformationLoaded = onInformationLoaded;
        this.onInformationUpdate = onInformationUpdate;
        this.preprocessPage = preprocessPage;
    }


}


var fragments = [
    new Fragment("General","../general/General.html",function(beaconInfo) {
        GeneralView.Actions.setBeaconInformation(beaconInfo);
    },null,null),
    new Fragment("Arbitrary data","../arbitrary/ArbitraryData.html",function(beaconInfo) {
        onArbitraryDataDocumentLoaded();
        initArbitraryData(beaconInfo);
    },
    function(beaconInfo) {
        initArbitraryData(beaconInfo);
    },null
    ),
    new Fragment("Parameters","../parameters/Parameters.html",function(beaconInfo) {
        ParametersLoader.setBeaconParameters(beaconInfo);
    },null,null)
]
const SLOT_PAGE_URL = "../slot/Slot.html";


$(document).ready(function() {



})

function openTab(evt, tabName) {
     console.log("open tab: "+tabName);

     var i, tabcontent, tablinks;
     tabcontent = document.getElementsByClassName("tabcontent");
     for (i = 0; i < tabcontent.length; i++) {
       tabcontent[i].style.display = "none";
     }
     tablinks = document.getElementsByClassName("tablinks");
     for (i = 0; i < tablinks.length; i++) {
       tablinks[i].className = tablinks[i].className.replace(" active", "");
     }
     document.getElementById(tabName).style.display = "block";
     evt.currentTarget.className += " active";
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

    addSlotFragmentsToFragmentsList(beaconInfo);
    loadViewPagerPagesWithBeaconInfo(beaconInfo)
    informationAlreadyLoaded = true;
}

function addSlotFragmentsToFragmentsList(beaconInfo) {
    let beacon = JSON.parse(beaconInfo);
    let slotCount = beacon.slots.slots.length;
    for(i = 0; i < slotCount; i++) {
        let slotName = "Slot "+(i+1);
        const slotIndex = i;
        let onInformationLoaded =
                function(beaconInfo) {
                    initSlot(beaconInfo,slotIndex);
                }
        let preprocessPage = function(pageContent) {
            return preprocessSlotPageForSlot(pageContent,slotIndex);
        }

        let frag = new Fragment(slotName,SLOT_PAGE_URL,onInformationLoaded,null,preprocessPage);
        fragments.push(frag);
    }
}

function preprocessSlotPageForSlot(pageContent,slotIndex) {
    let pageContentContainer = document.createElement("div");
    pageContentContainer.innerHTML = pageContent;
    let elements = pageContentContainer.getElementsByTagName("*");
    for(i = 0; i < elements.length; i++) {
        if(elements[i].id.length > 0) {
            elements[i].id += "-"+slotIndex;
        }
    }

    return pageContentContainer.innerHTML;
}

function loadViewPagerPagesWithBeaconInfo(beaconInfo) {
    var tabDiv = document.getElementById("tab-container");
    var fragmentsContainer = document.getElementById("fragments-container");
    console.log("loading pages");

    for (i = 0; i < fragments.length; i++) {
        const fragmentDiv = document.createElement("div");
        fragmentDiv.id = "fragment-"+i;
        fragmentDiv.classList.add("tabcontent");
        fragmentsContainer.appendChild(fragmentDiv);

        const fragment = fragments[i];

        $.get(fragment.pageUrl, function(data, status){
            console.log("loaded fragment "+fragment.pageUrl+" :data: "+data);
            if(fragment.preprocessPage != null) {
                data = fragment.preprocessPage(data);
                console.log("preprocessed content: "+data);
            }

            fragmentDiv.innerHTML = data;
            fragment.onInformationLoaded(beaconInfo);
        });


        var tabButton = document.createElement("button");
        tabButton.id = "tab-"+i;
        tabButton.classList.add("tablinks");
        tabButton.innerHTML = fragment.name;
        tabButton.setAttribute("onclick","openTab(event,'"+fragmentDiv.id+"')");
        tabDiv.appendChild(tabButton);

    }

    fragmentsContainer.style.height = (window.innerHeight - tabDiv.clientHeight)+"px";
    fragmentsContainer.style.width = (window.innerWidth)+"px";

}


const SWIPE_DIRECTION_RIGHT = 1;
const SWIPE_DIRECTION_LEFT = 2;
const SWIPE_DIRECTION_UP = 3;
const SWIPE_DIRECTION_DOWN = 4;
const MINIMAL_SWIPE_DISTANCE_FACTOR = 0.03;

function detectSwipeOnElement(elementSelector,onSwipeDetected) {
    var swipeStartX = -1;
    var swipeStartY = -1;

    $(elementSelector).on('pointermove', function (e) {
        let deltaX = e.clientX - swipeStartX;
        let deltaY = e.clientY - swipeStartY;

        let minimalSwipeDistance = MINIMAL_SWIPE_DISTANCE_FACTOR*window.innerWidth;
        let swipeDirection = 0;
        if(Math.abs(deltaX) > minimalSwipeDistance && Math.abs(deltaX) > Math.abs(deltaY)) {
            if(deltaX > 0) {
                swipeDirection = SWIPE_DIRECTION_RIGHT;
            } else {
                swipeDirection = SWIPE_DIRECTION_LEFT;
            }
        } else if(Math.abs(deltaY) > minimalSwipeDistance && Math.abs(deltaY) > Math.abs(deltaX)) {
            if(deltaY > 0) {
                swipeDirection = SWIPE_DIRECTION_DOWN;
            } else {
                swipeDirection = SWIPE_DIRECTION_UP;
            }
        } else {
            return;
        }

        onSwipeDetected(this,swipeDirection);

    })
    $(elementSelector).on('pointerdown', function (e) {
        swipeStartX = e.clientX;
        swipeStartY = e.clientY;
    })


}



