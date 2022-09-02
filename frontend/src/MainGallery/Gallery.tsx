import {NavLink} from "react-router-dom";


export default function Gallery() {
    return (
        <div>
            <NavLink to={"/upload"}>Upload new image</NavLink>
        </div>
    )
}