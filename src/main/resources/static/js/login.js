class SimpleAnimations {
    constructor() {
        console.log('🎨 Startig animations...');
        this.gridCanvas = document.getElementById('gridCanvas');
        this.stockCanvas = document.getElementById('stockCanvas');

        if (!this.gridCanvas || !this.stockCanvas) {
            console.error('❌ Canvas elements not found');
            return;
        }

        this.init();
    }

    init() {
        this.setupCanvases();
        this.animate();

        // Handle window resize
        window.addEventListener('resize', () => {
            console.log('🔄 Window resized, updating canvases...');
            this.setupCanvases();
        });
    }

    setupCanvases() {
        const width = window.innerWidth;
        const height = window.innerHeight;
        const dpr = window.devicePixelRatio || 1;

        [this.gridCanvas, this.stockCanvas].forEach(canvas => {
            // Set actual canvas size in pixels (accounting for device pixel ratio)
            canvas.width = width * dpr;
            canvas.height = height * dpr;

            // Set display size (CSS pixels)
            canvas.style.width = width + 'px';
            canvas.style.height = height + 'px';

            // Scale canvas back to fill the proper size
            canvas.getContext('2d').scale(dpr, dpr);
        });

        console.log('✅ Canvases setup:', width + 'x' + height, 'DPR:', dpr);
    }

    animate() {
        const gridCtx = this.gridCanvas.getContext('2d');
        const stockCtx = this.stockCanvas.getContext('2d');

        let time = 0;

        const draw = () => {
            time += 1;

            // Clear canvases
            gridCtx.clearRect(0, 0, this.gridCanvas.width, this.gridCanvas.height);
            stockCtx.clearRect(0, 0, this.stockCanvas.width, this.stockCanvas.height);

            // Draw animated grid
            this.drawGrid(gridCtx, time);

            // Draw animated lines
            this.drawLines(stockCtx, time);

            requestAnimationFrame(draw);
        };

        draw();
        console.log('✅ Animation loop started');
    }

    drawGrid(ctx, time) {
        const gridSize = 50;
        const width = window.innerWidth;
        const height = window.innerHeight;
        const cols = Math.ceil(width / gridSize);
        const rows = Math.ceil(height / gridSize);

        ctx.strokeStyle = 'rgba(0, 245, 255, 0.1)';
        ctx.lineWidth = 1;

        // Vertical lines
        for (let x = 0; x <= cols; x++) {
            const wave = Math.sin(time * 0.01 + x * 0.1) * 2;
            ctx.beginPath();
            ctx.moveTo(x * gridSize + wave, 0);
            ctx.lineTo(x * gridSize + wave, height);
            ctx.stroke();
        }

        // Horizontal lines
        for (let y = 0; y <= rows; y++) {
            const wave = Math.sin(time * 0.01 + y * 0.1) * 2;
            ctx.beginPath();
            ctx.moveTo(0, y * gridSize + wave);
            ctx.lineTo(width, y * gridSize + wave);
            ctx.stroke();
        }

        // Random glow points
        if (time % 60 === 0) {
            ctx.fillStyle = 'rgba(0, 245, 255, 0.8)';
            for (let i = 0; i < 3; i++) {
                const x = Math.random() * width;
                const y = Math.random() * height;
                ctx.beginPath();
                ctx.arc(x, y, 3, 0, Math.PI * 2);
                ctx.fill();
            }
        }
    }

    drawLines(ctx, time) {
        const width = window.innerWidth;
        const height = window.innerHeight;

        // Initialize line trails and camera system
        if (!this.lineTrails) {
            this.lineTrails = [
                {
                    color: '#00f5ff',
                    baseY: height * 0.25,
                    points: [],
                    maxTrailLength: width * 1.2,
                    absoluteX: 0, // Absolute position in world space
                    smoothY: height * 0.25 // Smoothed Y position
                },
                {
                    color: '#ff0080',
                    baseY: height * 0.5,
                    points: [],
                    maxTrailLength: width * 1.2,
                    absoluteX: 0,
                    smoothY: height * 0.5
                },
                {
                    color: '#ffff00',
                    baseY: height * 0.75,
                    points: [],
                    maxTrailLength: width * 1.2,
                    absoluteX: 0,
                    smoothY: height * 0.75
                }
            ];
            this.cameraX = 0; // Camera position for following the dots
            this.focusedTrail = 0; // Which trail to focus on
            this.switchTimer = 0;
        }

        // No more switching focus - track all trails equally

        // Sharp zigzag movement with very slightly slower speed
        const sharedTime = time * 0.02; // Same timing for transitions
        const sharedX = time * 2.8; // Very slightly slower for more crossovers

        this.lineTrails.forEach((trail, i) => {
            // All trails move together but with slight offsets for crossing
            trail.absoluteX = sharedX + i * 60;

            // 급격한 ㅅ자, V자 각진 지그재그 - 확 꺾이는 선
            const amplitude = 160;
            const segmentLength = 80; // 각 선분의 길이

            // 현재 세그먼트 위치
            const segmentPos = Math.floor((sharedX + i * 100) / segmentLength);
            const posInSegment = ((sharedX + i * 100) % segmentLength) / segmentLength;

            // 각 선마다 다른 패턴으로 서로 교차하도록
            let direction, nextDirection;

            if (i === 0) { // Cyan - 상승/하강 패턴
                const patterns = [1, -1, 1, 1, -1, -1, 1, -1, 1, -1, -1, 1];
                direction = patterns[segmentPos % patterns.length];
                nextDirection = patterns[(segmentPos + 1) % patterns.length];
            } else if (i === 1) { // Magenta - 다른 상승/하강 패턴
                const patterns = [-1, 1, -1, -1, 1, 1, -1, 1, -1, 1, 1, -1];
                direction = patterns[segmentPos % patterns.length];
                nextDirection = patterns[(segmentPos + 1) % patterns.length];
            } else { // Yellow - 또 다른 패턴
                const patterns = [1, 1, -1, 1, -1, -1, -1, 1, 1, -1, 1, -1];
                direction = patterns[segmentPos % patterns.length];
                nextDirection = patterns[(segmentPos + 1) % patterns.length];
            }

            // 선형 보간으로 ㅅ자, V자 형태 만들기
            let zigzagValue;
            if (posInSegment < 0.5) {
                // 첫 번째 반: 시작점에서 중간점으로 (상승 또는 하강)
                zigzagValue = direction * (posInSegment * 2);
            } else {
                // 두 번째 반: 중간점에서 끝점으로 (반대 방향)
                zigzagValue = direction * (2 - posInSegment * 2);
            }

            // 교차를 위한 베이스 오프셋 - 더 많은 역전을 위해 복잡한 패턴
            let baseOffset = 0;
            const slowTime = time * 0.004; // 조금 더 빠른 변화로 더 많은 교차
            const mediumTime = time * 0.001; // 매우 느린 큰 변화

            if (i === 0) { // Cyan - 복합 패턴으로 위아래 많이 움직임
                baseOffset = Math.sin(slowTime + i * 2.1) * 180 +
                            Math.cos(slowTime * 0.7 + i * 1.5) * 120 +
                            Math.sin(mediumTime + i * 3) * 300;
            } else if (i === 1) { // Magenta - 다른 복합 패턴
                baseOffset = Math.cos(slowTime + i * 2.8) * 220 +
                            Math.sin(slowTime * 1.3 + i * 2.2) * 160 +
                            Math.cos(mediumTime + i * 4) * 280;
            } else { // Yellow - 또 다른 복합 패턴
                baseOffset = Math.sin(slowTime + i * 1.7) * 200 +
                            Math.cos(slowTime * 0.9 + i * 3.1) * 140 +
                            Math.sin(mediumTime + i * 2.5) * 320;
            }

            const targetY = trail.baseY + baseOffset + zigzagValue * amplitude;

            // No smoothing for sharp triangular peaks
            trail.smoothY = targetY;

            // Add point to trail
            trail.points.push({
                x: trail.absoluteX,
                y: trail.smoothY,
                timestamp: time
            });

            // Remove old points based on distance
            trail.points = trail.points.filter(point =>
                trail.absoluteX - point.x < trail.maxTrailLength
            );
        });

        // Center camera on shared movement - all trails stay centered
        const targetCameraX = sharedX - width / 2;
        const cameraSmoothing = 0.08; // More responsive to keep all trails centered
        this.cameraX = this.cameraX + (targetCameraX - this.cameraX) * cameraSmoothing;

        // Apply camera transform
        ctx.save();
        ctx.translate(-this.cameraX, 0);

        this.lineTrails.forEach((trail, i) => {

            // Draw the dynamic trailing line (only where circle has been)
            if (trail.points.length > 1) {
                // Configure for razor-sharp, angular lines
                ctx.lineJoin = 'miter'; // Sharp corners - no rounding
                ctx.lineCap = 'butt'; // Flat ends for sharper angles
                ctx.miterLimit = 20; // Allow very sharp angles without clipping

                // Draw main line with sharp edges
                ctx.strokeStyle = trail.color;
                ctx.lineWidth = 4;
                ctx.globalAlpha = 0.9;

                ctx.beginPath();
                trail.points.forEach((point, index) => {
                    if (index === 0) {
                        ctx.moveTo(point.x, point.y);
                    } else {
                        ctx.lineTo(point.x, point.y);
                    }
                });
                ctx.stroke();

                // Add intense glow effect
                ctx.shadowBlur = 20;
                ctx.shadowColor = trail.color;
                ctx.lineWidth = 2;
                ctx.globalAlpha = 0.6;
                ctx.stroke();
                ctx.shadowBlur = 0;

                // Add gradient fade effect at the tail
                if (trail.points.length > 10) {
                    const gradient = ctx.createLinearGradient(
                        trail.points[0].x, trail.points[0].y,
                        trail.points[trail.points.length - 1].x, trail.points[trail.points.length - 1].y
                    );
                    gradient.addColorStop(0, trail.color + '20'); // Fade start
                    gradient.addColorStop(1, trail.color + 'FF'); // Bright end

                    ctx.strokeStyle = gradient;
                    ctx.lineWidth = 6;
                    ctx.globalAlpha = 0.4;
                    ctx.beginPath();
                    trail.points.forEach((point, index) => {
                        if (index === 0) {
                            ctx.moveTo(point.x, point.y);
                        } else {
                            ctx.lineTo(point.x, point.y);
                        }
                    });
                    ctx.stroke();
                }
            }

            // Draw the massive moving circle - data point
            const currentX = trail.absoluteX;
            const currentY = trail.smoothY;

            // Extra large outer glow (even bigger)
            ctx.fillStyle = trail.color;
            ctx.globalAlpha = 0.15;
            ctx.shadowBlur = 60;
            ctx.shadowColor = trail.color;
            ctx.beginPath();
            ctx.arc(currentX, currentY, 35, 0, Math.PI * 2); // Massive size
            ctx.fill();

            // Large glow ring
            ctx.shadowBlur = 45;
            ctx.fillStyle = trail.color;
            ctx.globalAlpha = 0.4;
            ctx.beginPath();
            ctx.arc(currentX, currentY, 25, 0, Math.PI * 2);
            ctx.fill();

            // Bright middle ring
            ctx.shadowBlur = 30;
            ctx.fillStyle = trail.color;
            ctx.globalAlpha = 0.8;
            ctx.beginPath();
            ctx.arc(currentX, currentY, 18, 0, Math.PI * 2);
            ctx.fill();

            // Solid color ring
            ctx.shadowBlur = 20;
            ctx.fillStyle = trail.color;
            ctx.globalAlpha = 1;
            ctx.beginPath();
            ctx.arc(currentX, currentY, 12, 0, Math.PI * 2);
            ctx.fill();

            // Bright white core
            ctx.shadowBlur = 10;
            ctx.fillStyle = '#ffffff';
            ctx.globalAlpha = 1;
            ctx.beginPath();
            ctx.arc(currentX, currentY, 8, 0, Math.PI * 2); // Large bright core
            ctx.fill();

            // Dynamic pulsing effect - all circles get equal treatment
            const pulse = Math.sin(time * 0.3 + i) * 3 + 5;
            ctx.shadowBlur = 0;
            ctx.fillStyle = trail.color;
            ctx.globalAlpha = 0.7;
            ctx.beginPath();
            ctx.arc(currentX, currentY, pulse, 0, Math.PI * 2);
            ctx.fill();

            // Equal highlight for all trails
            ctx.strokeStyle = 'rgba(255, 255, 255, 0.4)';
            ctx.lineWidth = 2;
            ctx.globalAlpha = 0.6;
            ctx.shadowBlur = 10;
            ctx.shadowColor = trail.color;
            ctx.beginPath();
            ctx.arc(currentX, currentY, 22, 0, Math.PI * 2);
            ctx.stroke();
        });

        // Restore camera transform
        ctx.restore();

        ctx.globalAlpha = 1;
        ctx.shadowBlur = 0;
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    console.log('📄 DOM loaded, starting animations...');
    new SimpleAnimations();
});

// Form submission handling
document.querySelector('.form').addEventListener('submit', function(e) {
    console.log('📤 Form submitted');
    document.querySelector('.btn-text').innerHTML = 'Loading...';
});