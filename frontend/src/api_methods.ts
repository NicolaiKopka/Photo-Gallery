import axios from "axios";

export function uploadToCloudinary(formData: FormData) {
    return axios.post("https://api.cloudinary.com/v1_1/drzar88fa/image/upload", formData)
        .then(response => response.data)
}