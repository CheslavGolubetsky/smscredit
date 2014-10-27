<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="outerHeader.jsp" %>

<div class="container" style="margin-top: 30px;">
    <div class="row">
        <div class="span10 offset1">
            <div class="alert alert-block alert-error fade in">
                <h4 class="alert-heading"><c:out value="${errorHeader}"/></h4>
                <p><c:out value="${errorMessage}"/></p>
            </div>
        </div>
    </div>
</div>

<%@ include file="outerFooter.jsp" %>