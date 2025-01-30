// utils/RoutePlanner.js
export function useRoutePlanner() {
    const directionsService = new google.maps.DirectionsService();
    const directionsRenderer = new google.maps.DirectionsRenderer();

    const initRoutePlanner = (map) => {
        directionsRenderer.setMap(map);
    };

    const calculateRoute = (origin, destination, travelMode = "DRIVING") => {
        const request = {
            origin: origin,
            destination: destination,
            travelMode: google.maps.TravelMode[travelMode], // 可選 WALKING, BICYCLING 等
        };

        return new Promise((resolve, reject) => {
            directionsService.route(request, (response, status) => {
                if (status === google.maps.DirectionsStatus.OK) {
                    directionsRenderer.setDirections(response);
                    resolve(response);
                } else {
                    reject(status);
                }
            });
        });
    };

    return { initRoutePlanner, calculateRoute };
}
