<!DOCTYPE html>
<html>
<head>
  <title>Footprints</title>
  <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/stylesheets/login.css">
</head>
<body>
<div class="loginContainer">
  <div class="upperContainer">
    <div class="title">
      <span class="agency">RealityForge</span> Footprints
      <div class="text">
        Test Site for JEE stuff
      </div>
    </div>
  </div>
  <div class="lowerContainer">
    <div class="logout">
      <div class="message">
        You are now logged out
      </div>
      <div>
        <a id="login-link" href="${pageContext.request.contextPath}/">Return to login page</a>
      </div>
    </div>
  </div>
  <div class="footer">
    <img src="${pageContext.request.contextPath}/images/help.png" alt="Help"/>

    <div class="text">
      For help with accounts please contact the Super support
      <a href="mailto:support@example.com">support@example.com</a>
    </div>
  </div>
</div>
</body>
</html>
