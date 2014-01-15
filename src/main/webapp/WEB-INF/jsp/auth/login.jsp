<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <div class="description">
      Login <span class="instruction">using your fake credentials</span>
    </div>

    <div class="formContainer">
      <c:if test='${it == "error"}'>
        <div class="error">
          Invalid username or password
        </div>
      </c:if>
      <form method="post">
        <div>
          <label id="j_usernameLabel" for="j_username">Username</label>
        </div>
        <div>
          <input type="text" name="j_username" id="j_username" tabindex="1" autocomplete="off"/>
        </div>
        <div>
          <label id="j_passwordLabel" for="j_password">Password</label>
        </div>
        <div>
          <input type="password" name="j_password" id="j_password" tabindex="2" autocomplete="off"/>
        </div>
        <div class="button">
          <input id="j_submitButton" type="submit" value="Login"/>
        </div>
      </form>
    </div>
  </div>
  <div class="footer">
    <img src="${pageContext.request.contextPath}/images/help.png" alt="Help"/>

    <div class="text">
      For help with accounts please contact the Super support <a href="mailto:support@example.com">support@example.com</a>
    </div>
  </div>
</div>
</body>
</html>