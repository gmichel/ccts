USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_NationalUpdateInsertTermMatrix]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		gjm
-- =============================================
CREATE PROCEDURE [svm].[SP_NationalUpdateInsertTermMatrix]
	@trainingType  varchar(50),
	@term_matrix  varchar(max)
	AS
BEGIN
	SET NOCOUNT ON;

   UPDATE svm.NationalTermMatrix SET term_matrix = @term_matrix 
    where term_matrix_type = @trainingType 
	IF @@ROWCOUNT = 0
	BEGIN      
		   INSERT INTO svm.NationalTermMatrix VALUES(@trainingType,@term_matrix)
    END 
END
GO
