import React, { useState, useEffect } from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Gallery from "./MainGallery/Gallery"
import Upload from "./Upload/Upload";

function App() {

    return (
        <BrowserRouter>
            <Routes>
                <Route path={"/"} element={<Gallery/>}/>
                <Route path={"/upload"} element={<Upload/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
