function Scan() {
    return (
        <div style={{ maxWidth: "480px", margin: "60px auto", padding: "24px" }}>
            <h2>Scan QR Code</h2>
            <p>Upload an image or use your camera to scan a QR code.</p>

            {/* TODO: implement file upload / webcam scanning with ZXing-js */}
            <input
                type="file"
                accept="image/*"
                style={{ marginTop: "16px" }}
                onChange={(e) => console.log("File selected:", e.target.files[0])}
            />
        </div>
    );
}

export default Scan;
