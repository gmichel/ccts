USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_UpdateInsertDocumentTermVector]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		gjm
-- =============================================
CREATE PROCEDURE [svm].[SP_UpdateInsertDocumentTermVector]

	@documentTerms varchar(max),
	@docId int
	AS
BEGIN
	SET NOCOUNT ON;

   UPDATE svm.DocumentTermVector SET term_vector = @documentTerms 
    where document_id = @docId
	IF @@ROWCOUNT = 0
	BEGIN
          INSERT INTO svm.DocumentTermVector VALUES(@documentTerms,@docId)
    END 
END
GO
