// utils/placeManager.js

// 初始化地圖
export const initMap = (mapElement, mapConfig) => {
    return new google.maps.Map(mapElement, mapConfig);
};

// 更新標記並顯示資訊框
export const updateMarker = (map, position, place, markers) => {
    const marker = new google.maps.Marker({
        map,
        position,
        animation: google.maps.Animation.DROP,
    });

    const infoWindow = new google.maps.InfoWindow({
        content: `<div><h3>${place.placeName}</h3><p>${place.placeAddress}</p></div>`,
    });

    marker.addListener("animation_changed", () => {
        if (marker.getAnimation() === null) {
            infoWindow.open(map, marker);
        }
    });

    markers.push(marker);
};

// 格式化價格等資料
export const convertPriceLevel = (priceLevel) => {
    switch (priceLevel) {
        case 0:
            return "便宜";
        case 1:
            return "平價";
        case 2:
            return "中等";
        case 3:
            return "高級";
        case 4:
            return "奢華";
        default:
            return null;
    }
};

// 格式化營業時間資料
export const convertBusinessHours = (openingHours) => {
    if (!openingHours) return null;
    return openingHours.split("\n").reduce((acc, line) => {
        const [day, hours] = line.split(": ");
        if (day && hours) {
            acc[day.trim()] = hours.trim();
        }
        return acc;
    }, {});
};

// 格式化地點資料
export const formatPlaceDetails = (place) => {
    const updatedPlace = {
        displayName: place.placeName || null,
        formattedAddress: place.placeAddress || null,
        location: { lat: place.latitude, lng: place.longitude },
        rating: place.rating || null,
        openingHours: convertBusinessHours(place.businessHours) || null,
        photos: place.photos || [],
        types: place.placeType || null,
        formattedPhoneNumber: place.placePhone || null,
        website: place.website || null,
        priceLevel: convertPriceLevel(place.priceLevel) || null,
        addressComponents: place.address_components || [],
        reservation: place.reservation || null,
    };
    return updatedPlace;
};

// 路線規劃函式
export const calculateRoute = (map, origin, destination) => {
    const directionsService = new google.maps.DirectionsService();
    const directionsRenderer = new google.maps.DirectionsRenderer({
        map: map,
    });

    const request = {
        origin: origin,
        destination: destination,
        travelMode: google.maps.TravelMode.DRIVING, // 可以是 DRIVING, WALKING, BICYCLING, TRANSIT
        provideRouteAlternatives: true, // 顯示替代路線
    };

    // 發送請求並處理回應
    directionsService.route(request, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
            directionsRenderer.setDirections(result);
        } else {
            console.error("路線規劃失敗:", status);
        }
    });
};
