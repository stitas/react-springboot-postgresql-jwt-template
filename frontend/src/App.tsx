import { BrowserRouter, Routes, Route } from "react-router-dom";
import IndexPage from "./pages/Index";
import RegistrationPage from "./pages/Registration";
import LoginPage from "./pages/Login";
import PasswordResetPage from "./pages/PasswordReset";
import PasswordResetEmailPage from "./pages/PasswordResetEmailInput";
import NotFound from "./pages/NotFound";
import ProfilePage from "./pages/Profile";
import OauthTokenRedirect from "./pages/OauthTokenRedirect";

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<IndexPage/>}/>
          <Route path="/sign-up" element={<RegistrationPage/>}/>
          <Route path="/login" element={<LoginPage/>}/>
          <Route path="/password-reset-email" element={<PasswordResetEmailPage/>}/>
          <Route path="/password-reset/:token" element={<PasswordResetPage/>}/>
          <Route path="/profile" element={<ProfilePage/>}/>
          <Route path="/oauth2/success" element={<OauthTokenRedirect/>}/>
          <Route path="*" element={<NotFound/>}/>
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
