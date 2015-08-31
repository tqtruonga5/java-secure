<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="rb" uri="http://rbh.kms.challenges.com/customtags"%>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>
<t:layout>
    <jsp:attribute name="header"><fmt:message key="rabbitholes.homepage.title"/></jsp:attribute>
    <jsp:body>
        <div class="panel panel-default">
            <div class="panel-body">
                <h3>Welcome to the world of files!!!</h3>
                <div>
                    <c:choose>
                        <c:when test="${fn:length(files) eq 0}">
                            <span>It's a bit cold here as no file is available at the moment!</span>
                            <span>Please <a href="/upload">upload</a> your file!</span>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <form action="/search" method="GET">
                                    <div class="row">
                                        <div id="form-group-file" class="form-group col-lg-4">
                                            <label>Search: </label>
                                            <input type="text" name="searchText" class="form-inline">
                                            <input type="submit" value="Search" class="btn btn-default">
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <c:forEach var="file" items="${files}">
                                <p>
                                    <span>${e:forHtmlContent(file.uploader.firstName) }</span> <span>: </span>
                                    <span><a href="/download?fileName=${file.fileName}&userId=${file.uploader.id}">${file.fileName}</a></span>
                                    <span>${e:forHtmlContent(file.uploadNote) }</span>
                                    <rb:authenticate access="${user.role}" principleName="ADMIN">
                                        <a href="${pageContext.request.contextPath}/delete?fileId=${file.id}&r=home">Delete</a>
                                    </rb:authenticate>
                                </p>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </jsp:body>
</t:layout>