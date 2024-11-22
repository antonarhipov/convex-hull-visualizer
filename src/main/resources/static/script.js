const pointsInput = document.getElementById('pointsInput');
const calculateButton = document.getElementById('calculateButton');
const randomPointsButton = document.getElementById('randomPointsButton');
const graphCanvas = document.getElementById('graphCanvas');
let chart;

calculateButton.addEventListener('click', calculateConvexHull);
randomPointsButton.addEventListener('click', generateRandomPoints);

function calculateConvexHull() {
    try {
        const points = JSON.parse(pointsInput.value);
        fetch('/points', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(points),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(convexHull => {
                drawGraph(points, convexHull);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred. Check the console for details.');
            });
    } catch (error) {
        alert('Invalid JSON input. Please check your input format.');
    }
}

function generateRandomPoints() {
    const numPoints = 10;
    const maxCoord = 100;
    const points = Array.from({ length: numPoints }, () => ({
        x: Math.floor(Math.random() * maxCoord),
        y: Math.floor(Math.random() * maxCoord)
    }));
    pointsInput.value = JSON.stringify(points, null, 2);
}

function drawGraph(inputPoints, convexHullPoints) {
    if (chart) {
        chart.destroy();
    }

    // Connect the last point in the convex hull to the first point
    if (convexHullPoints.length > 1) {
        convexHullPoints.push(convexHullPoints[0]);
    }

    const ctx = graphCanvas.getContext('2d');
    chart = new Chart(ctx, {
        type: 'scatter',
        data: {
            datasets: [
                {
                    label: 'Input Points',
                    data: inputPoints,
                    backgroundColor: 'rgba(52, 152, 219, 0.8)',
                    pointRadius: 6,
                },
                {
                    label: 'Convex Hull',
                    data: convexHullPoints,
                    backgroundColor: 'rgba(231, 76, 60, 0.8)',
                    borderColor: 'rgba(231, 76, 60, 1)',
                    pointRadius: 6,
                    showLine: true,
                    fill: false,
                    tension: 0,
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    type: 'linear',
                    position: 'bottom',
                    title: {
                        display: true,
                        text: 'X Coordinate'
                    }
                },
                y: {
                    type: 'linear',
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Y Coordinate'
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return `(${context.parsed.x}, ${context.parsed.y})`;
                        }
                    }
                }
            }
        }
    });
}

// Initialize with random points
generateRandomPoints();