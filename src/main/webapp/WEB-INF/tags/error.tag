<!DOCTYPE html>
<%@ tag display-name="error" pageEncoding="UTF-8"%>
<%@ attribute name="fieldName"%>
<%@ attribute name="id"%>
<%@ attribute name="cssClass"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="rbt" uri="http://rbh.kms.challenges.com/customtags" %>
<style>
    .help-block{
        margin-top: 0px;
    }
    .error{
        color: red;
    }
</style>
<fmt:setBundle basename="i18n.message" scope="request"/>
<c:if test="${validationErrors!=null&&validationErrors.containsKey(fieldName)}">
    <label id="${id}" class="${cssClass} error">${validationErrors.get(fieldName).getErrorMessage()}</label>
</c:if>