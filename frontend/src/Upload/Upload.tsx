import {useState} from "react";
import {uploadToCloudinary} from "../api_methods";


export default function Upload() {
    const [img, setImg] = useState({} as File)
    const [imgUrl, setImgUrl] = useState("")

    const handleUpload = () => {
        const formData = new FormData()
        formData.append("file", img)
        formData.append("upload_preset", "images_upload")

        uploadToCloudinary(formData).then(data => setImgUrl(data.secure_url))
    }

    return (
        <div>
            <input type={"file"} accept={"image/*"} onChange={ev => {
                if(ev.target.files !== null) {
                    setImg(ev.target.files[0])
                }
            }}/>
            <button onClick={handleUpload}>Upload</button>
            {imgUrl && <img src={imgUrl}/>}
        </div>
    )
}