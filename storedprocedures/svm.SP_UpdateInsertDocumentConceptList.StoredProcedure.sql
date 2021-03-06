USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_UpdateInsertDocumentConceptList]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		gjm
-- =============================================
CREATE PROCEDURE [svm].[SP_UpdateInsertDocumentConceptList]

	@documentList varchar(max),
	@docId int

	AS
BEGIN
	SET NOCOUNT ON;

   UPDATE svm.DocumentConceptList SET DocumentConceptList = @documentList 
    where documentid = @docId
	IF @@ROWCOUNT = 0
	BEGIN
          INSERT INTO svm.DocumentConceptList VALUES(@documentList,@docId)
    END 
END
GO
