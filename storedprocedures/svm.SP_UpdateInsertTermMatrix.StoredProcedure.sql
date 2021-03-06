USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_UpdateInsertTermMatrix]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		gjm
-- =============================================
CREATE PROCEDURE [svm].[SP_UpdateInsertTermMatrix]

	@station int,
	@trainingType  varchar(50),
	@term_matrix  varchar(max)
	AS
BEGIN
	SET NOCOUNT ON;

   UPDATE svm.TermMatrix SET term_matrix = @term_matrix 
    where term_matrix_type = @trainingType and term_matrix_station = @station
	IF @@ROWCOUNT = 0
	BEGIN      
		   INSERT INTO svm.TermMatrix VALUES(@station,@trainingType,@term_matrix)
    END 
END
GO
